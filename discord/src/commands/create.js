const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const decode = require('jwt-decode');
const { logger } = require('../utils');
const axios = require('axios');
const fs = require("fs");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('create')
        .setDescription('Creates a new assignment or peer review or team')
        .addSubcommand(subcommand => subcommand
            .setName('homework')
            .setDescription('Creates a homework assignment')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the homework assignment')
                .setRequired(true))
            .addStringOption(option => option
                .setName('instructions')
                .setDescription('The instructions for the homework assignment')
                .setRequired(true))
            .addStringOption(option => option
                .setName('due-date')
                .setDescription('The YYYY-MM-DD due date for the homework assignment')
                .setRequired(true))
            .addIntegerOption(option => option
                .setName('points')
                .setDescription('The total points for the homework assignment')
                .setRequired(true))
            .addAttachmentOption(option => option
                .setName('file')
                .setDescription('The file to upload for the homework assignment')))
        .addSubcommand(subcommand => subcommand
            .setName('peer-review')
            .setDescription('Creates a peer review assignment')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the homework assignment to associate with')
                .setRequired(true))
            .addStringOption(option => option
                .setName('instructions')
                .setDescription('The instructions for the peer review assignment')
                .setRequired(true))
            .addStringOption(option => option
                .setName('due-date')
                .setDescription('The YYYY-MM-DD due date for the peer review assignment')
                .setRequired(true))
            .addIntegerOption(option => option
                .setName('points')
                .setDescription('The total points for the peer review assignment')
                .setRequired(true))
            .addAttachmentOption(option => option
                .setName('rubric')
                .setDescription('The rubric to upload for the peer review assignment'))
            .addAttachmentOption(option => option
                .setName('template')
                .setDescription('The template to upload for the peer review assignment')))
        .addSubcommand(subcommand => subcommand
            .setName('team')
            .setDescription('Creates a team')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the team')
                .setRequired(true))),

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {
            const token = interaction.client.accounts.get(interaction.user.id);
            if (!token) {
                const commands = await interaction.guild.commands.fetch();
                const command = commands.find(cmd => cmd.name === 'login');

                await interaction.reply(`You are not logged in. Please login with </login:${command.id}>.`, { ephemeral: true });
                return;
            }

            const role = decode(token).groups[0];
            const config = {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            };

            const courseId = await axios
                .get('http://course-viewer:13128/view/professor/courses', config)
                .then((res) => {
                    return res.data.find(course => course.course_name === interaction.guild.name).course_id;
                })
                .catch((err) => {
                    logger.error(err.stack);
                });

            const subcommand = interaction.options.getSubcommand();

            if (subcommand === 'homework') {
                if (role !== 'professor') {
                    await interaction.reply('You are not authorized to create homework assignments.');
                    return;
                }

                const name = interaction.options.getString('name');
                const instructions = interaction.options.getString('instructions');
                const dueDate = interaction.options.getString('due-date');
                const points = interaction.options.getInteger('points');
                const file = interaction.options.getAttachment('file');

                const url = 'http://professor-assignment:13130/assignments/professor/courses';
                const data = {
                    course_id: courseId,
                    assignment_name: name,
                    instructions: instructions,
                    due_date: dueDate,
                    points: points
                };

                await axios
                    .post(`${url}/create-assignment-no-peer-review`, data, config)
                    .catch(async (err) => {
                        await interaction.reply('Error creating homework assignment.');
                        logger.error(err.stack);
                    });

                if (file) {
                    const assignmentId = await axios
                        .get(`${url}/${courseId}/assignments`, config)
                        .then((res) => {
                            return res.data.find(assignment => assignment.assignment_name === name).assignment_id;
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    const base64File = await axios
                        .get(file.url, { responseType: 'arraybuffer', responseEncoding: 'base64url' })
                        .then((res) => {
                            let temp = Buffer.from(res.data, 'base64url').toString('base64url');
                            temp = temp.replace('data:', '').replace(/^.+,/, '');
                            return Buffer.from(temp, 'base64url');
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    fs.writeFileSync(file.name, base64File.toString('base64url'), { encoding: 'base64url' });
                    const fileActual = fs.readFileSync(file.name, { encoding: 'base64' });

                    const fileData = new FormData();
                    fileData.append(file.name, fileActual);

                    await axios
                        .post(`${url}/${courseId}/assignments/${assignmentId}/upload`, fileData, config)
                        .catch(async (err) => {
                            logger.error(err.stack);
                        });
                }

                await interaction.reply('Homework assignment created!');

            } else if (subcommand === 'peer-review') {
                if (role !== 'professor') {
                    await interaction.reply('You are not authorized to create peer review assignments.');
                    return;
                }

                const name = interaction.options.getString('name');
                const instructions = interaction.options.getString('instructions');
                const dueDate = interaction.options.getString('due-date');
                const points = interaction.options.getInteger('points');
                const rubric = interaction.options.getAttachment('rubric');
                const template = interaction.options.getAttachment('template');

                const url = `http://professor-assignment:13130/assignments/professor/courses/${courseId}`;
                const data = {
                    peer_review_instructions: instructions,
                    peer_review_due_date: dueDate,
                    peer_review_points: points
                };

                const assignmentId = await axios
                    .get(`${url}/assignments`, config)
                    .then((res) => {
                        return res.data.find(assignment => assignment.assignment_name === name).assignment_id;
                    })
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                await axios
                    .put(`${url}/assignments/${assignmentId}/addPeerReviewData`, data, config)
                    .catch(async (err) => {
                        await interaction.reply('Error creating peer review.');
                        logger.error(err.stack);
                    });

                if (rubric) {
                    const base64File = await axios
                        .get(rubric.url, { responseType: 'arraybuffer', responseEncoding: 'base64url' })
                        .then((res) => {
                            let temp = Buffer.from(res.data, 'base64url').toString('base64url');
                            temp = temp.replace('data:', '').replace(/^.+,/, '');
                            return Buffer.from(temp, 'base64url');
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    fs.writeFileSync(rubric.name, base64File.toString('base64url'), { encoding: 'base64url' });
                    const file = fs.readFileSync(rubric.name, { encoding: 'base64' });

                    const rubricData = new FormData();
                    rubricData.append(rubric.name, file);

                    await axios
                        .post(`${url}/assignments/${assignmentId}/peer-review/rubric/upload`, rubricData, config)
                        .catch(async (err) => {
                            logger.error(err.stack);
                        });
                }

                if (template) {
                    const base64File = await axios
                        .get(template.url, { responseType: 'arraybuffer', responseEncoding: 'base64url' })
                        .then((res) => {
                            let temp = Buffer.from(res.data, 'base64url').toString('base64url');
                            temp = temp.replace('data:', '').replace(/^.+,/, '');
                            return Buffer.from(temp, 'base64url');
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    fs.writeFileSync(template.name, base64File.toString('base64url'), { encoding: 'base64url' });
                    const file = fs.readFileSync(template.name, { encoding: 'base64' });

                    const templateData = new FormData();
                    templateData.append(template.name, file);

                    await axios
                        .post(`${url}/assignments/${assignmentId}/peer-review/template/upload`, templateData, config)
                        .catch(async (err) => {
                            logger.error(err.stack);
                        });
                }

                await interaction.reply('Peer review assignment created!');

            } else if (subcommand === 'team') {
                const name = interaction.options.getString('name');

                if (name.split(' ').length > 1) {
                    await interaction.reply('Please enter a team name with no spaces.');
                    return;
                }

                if (name === '') {
                    await interaction.reply('Team name cannot be empty.');
                    return;
                }

                if (name.length > 20) {
                    await interaction.reply('Team name is too long.');
                    return;
                }

                const data = {
                    course_id: courseId,
                    student_id: interaction.member.displayName,
                    team_name: name
                };

                await axios
                    .post('http://peer-review-teams:13129/teams/team/create', data, config)
                    .catch(async (err) => {
                        await interaction.reply('Error creating team.');
                        logger.error(err.stack);
                    });

                await interaction.reply('Team created!');
            }

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
