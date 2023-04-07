const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger, authenticate } = require('../utils');
const axios = require('axios');

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
            const subcommand = interaction.options.getSubcommand();
            const { headers, roles, courseId } = await authenticate(interaction)
                .catch(async (err) => {
                    await interaction.reply({ content: err.message, ephemeral: true });
                    logger.error('User not logged in');
                });

            if (subcommand === 'homework') {
                const name = interaction.options.getString('name');
                const instructions = interaction.options.getString('instructions');
                const dueDate = interaction.options.getString('due-date');
                const points = interaction.options.getInteger('points');
                const file = interaction.options.getAttachment('file');

                if (!roles.includes('professor')) {
                    await interaction.reply({ content: 'You are not authorized to create homework assignments!', ephemeral: true });
                    return;
                }

                const url = 'http://professor-assignment:13130/assignments/professor/courses';
                const data = {
                    course_id: courseId,
                    assignment_name: name,
                    instructions: instructions,
                    due_date: dueDate,
                    points: points
                };

                await axios
                    .post(`${url}/create-assignment-no-peer-review`, data, headers)
                    .catch(async (err) => {
                        await interaction.reply({ content: 'Error creating homework assignment!', ephemeral: true });
                        logger.error(err.stack);
                    });

                if (file) {
                    const assignmentId = await axios
                        .get(`${url}/${courseId}/assignments`, headers)
                        .then((res) => {
                            return res.data.find(assignment => assignment.assignment_name === name).assignment_id;
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    const base64File = await axios
                        .get(file.url, { responseType: 'arraybuffer', responseEncoding: 'base64' })
                        .then((res) => {
                            return Buffer.from(res.data, 'base64').toString('base64');
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    const fileData = new FormData();
                    fileData.append(file.name, base64File);

                    await axios
                        .post(`${url}/${courseId}/assignments/${assignmentId}/upload`, fileData, headers)
                        .catch((err) => {
                            logger.error(err.stack);
                        });
                }

                await interaction.reply('Homework assignment created!');

            } else if (subcommand === 'peer-review') {
                const name = interaction.options.getString('name');
                const instructions = interaction.options.getString('instructions');
                const dueDate = interaction.options.getString('due-date');
                const points = interaction.options.getInteger('points');
                const rubric = interaction.options.getAttachment('rubric');
                const template = interaction.options.getAttachment('template');

                if (!roles.includes('professor')) {
                    await interaction.reply({ content: 'You are not authorized to create peer review assignments!', ephemeral: true });
                    return;
                }

                const url = `http://professor-assignment:13130/assignments/professor/courses/${courseId}`;
                const data = {
                    peer_review_instructions: instructions,
                    peer_review_due_date: dueDate,
                    peer_review_points: points
                };

                const assignmentId = await axios
                    .get(`${url}/assignments`, headers)
                    .then((res) => {
                        return res.data.find(assignment => assignment.assignment_name === name).assignment_id;
                    })
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                await axios
                    .put(`${url}/assignments/${assignmentId}/addPeerReviewData`, data, headers)
                    .catch(async (err) => {
                        await interaction.reply({ content: 'Error creating peer review!', ephemeral: true });
                        logger.error(err.stack);
                    });

                if (rubric) {
                    const base64File = await axios
                        .get(rubric.url, { responseType: 'arraybuffer', responseEncoding: 'base64' })
                        .then((res) => {
                            return Buffer.from(res.data, 'base64').toString('base64');
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    const rubricData = new FormData();
                    rubricData.append(rubric.name, base64File);

                    await axios
                        .post(`${url}/assignments/${assignmentId}/peer-review/rubric/upload`, rubricData, headers)
                        .catch((err) => {
                            logger.error(err.stack);
                        });
                }

                if (template) {
                    const base64File = await axios
                        .get(template.url, { responseType: 'arraybuffer', responseEncoding: 'base64' })
                        .then((res) => {
                            return Buffer.from(res.data, 'base64').toString('base64');
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    const templateData = new FormData();
                    templateData.append(template.name, base64File);

                    await axios
                        .post(`${url}/assignments/${assignmentId}/peer-review/template/upload`, templateData, headers)
                        .catch((err) => {
                            logger.error(err.stack);
                        });
                }

                await interaction.reply('Peer review assignment created!');

            } else if (subcommand === 'team') {
                const name = interaction.options.getString('name');

                if (name.split(' ').length > 1) {
                    await interaction.reply({ content: 'Please enter a team name with no spaces!', ephemeral: true });
                    return;
                }

                if (name === '') {
                    await interaction.reply({ content: 'Team name cannot be empty!', ephemeral: true });
                    return;
                }

                if (name.length > 20) {
                    await interaction.reply({ content: 'Team name is too long!', ephemeral: true });
                    return;
                }

                const data = {
                    course_id: courseId,
                    student_id: interaction.member.displayName,
                    team_name: name
                };

                await axios
                    .post('http://peer-review-teams:13129/teams/team/create', data, headers)
                    .catch(async (err) => {
                        await interaction.reply('Error creating team!');
                        logger.error(err.stack);
                    });

                await interaction.reply('Team created!');
            }

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
