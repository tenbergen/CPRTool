const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const decode = require("jwt-decode");
const { logger } = require('../utils');
const axios = require("axios");
const fs = require("fs");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('edit')
        .setDescription('Edits an assignment or peer review or team')
        .addSubcommand(subcommand => subcommand
            .setName('homework')
            .setDescription('Edits a homework assignment')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the homework assignment to edit')
                .setRequired(true))
            .addStringOption(option => option
                .setName('new-name')
                .setDescription('The new name for the homework assignment'))
            .addStringOption(option => option
                .setName('instructions')
                .setDescription('The new instructions for the homework assignment'))
            .addStringOption(option => option
                .setName('due-date')
                .setDescription('The YYYY-MM-DD new due date for the homework assignment'))
            .addIntegerOption(option => option
                .setName('points')
                .setDescription('The new total points for the homework assignment'))
            .addAttachmentOption(option => option
                .setName('add-file')
                .setDescription('The file to upload for the homework assignment'))
            .addStringOption(option => option
                .setName('remove-file')
                .setDescription('The file to remove from the homework assignment')))
        .addSubcommand(subcommand => subcommand
            .setName('peer-review')
            .setDescription('Edits a peer review assignment')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the associated assignment')
                .setRequired(true))
            .addStringOption(option => option
                .setName('instructions')
                .setDescription('The new instructions for the peer review assignment'))
            .addStringOption(option => option
                .setName('due-date')
                .setDescription('The YYYY-MM-DD new due date for the peer review assignment'))
            .addIntegerOption(option => option
                .setName('points')
                .setDescription('The new total points for the peer review assignment'))
            .addAttachmentOption(option => option
                .setName('add-rubric')
                .setDescription('The rubric to upload for the peer review assignment'))
            .addAttachmentOption(option => option
                .setName('add-template')
                .setDescription('The template to upload for the peer review assignment'))
            .addStringOption(option => option
                .setName('remove-rubric')
                .setDescription('The rubric to remove from the peer review assignment'))
            .addStringOption(option => option
                .setName('remove-template')
                .setDescription('The template to remove from the peer review assignment')))
        .addSubcommand(subcommand => subcommand
            .setName('team')
            .setDescription('Edits a team')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the team to edit')
                .setRequired(true))
            .addStringOption(option => option
                .setName('new-name')
                .setDescription('The new name for the team'))
            .addUserOption(option => option
                .setName('add-student')
                .setDescription('The student to add to the team'))
            .addUserOption(option => option
                .setName('remove-student')
                .setDescription('The student to remove from the team')))
        .addSubcommand(subcommand => subcommand
            .setName('course')
            .setDescription('Edits a course')
            .addStringOption(option => option
                .setName('add-student')
                .setDescription('The first and last name as well as the email of the student to add'))
            .addUserOption(option => option
                .setName('remove-student')
                .setDescription('The name of the student to remove'))),

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
                    await interaction.reply('You are not authorized to edit homework assignments.');
                    return;
                }

                const name = interaction.options.getString('name');
                const newName = interaction.options.getString('new-name');
                const instructions = interaction.options.getString('instructions');
                const dueDate = interaction.options.getString('due-date');
                const points = interaction.options.getInteger('points');
                const addFile = interaction.options.getAttachment('add-file');
                const removeFile = interaction.options.getString('remove-file');

                const url = `http://professor-assignment:13130/assignments/professor/courses/${courseId}`;
                const assignment = await axios
                    .get(`${url}/assignments`, config)
                    .then((res) => {
                        return res.data.find(assignment => assignment.assignment_name === name);
                    })
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                const assignmentId = assignment.assignment_id;
                const data = {
                    course_id: courseId,
                    assignment_name: name,
                    instructions: assignment.instructions,
                    due_date: assignment.due_date,
                    points: assignment.points
                };

                if (newName) data.assignment_name = newName;
                if (instructions) data.instructions = instructions;
                if (dueDate) data.due_date = dueDate;
                if (points) data.points = points;

                await axios
                    .put(`${url}/assignments/${assignmentId}/editNoPeerReview`, data, config)
                    .catch(async (err) => {
                        await interaction.reply('Error editing homework assignment.');
                        logger.error(err.stack);
                    });

                if (addFile) {
                    const base64File = await axios
                        .get(addFile.url, { responseType: 'arraybuffer', responseEncoding: 'base64url' })
                        .then((res) => {
                            let temp = Buffer.from(res.data, 'base64url').toString('base64url');
                            temp = temp.replace('data:', '').replace(/^.+,/, '');
                            return Buffer.from(temp, 'base64url');
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    fs.writeFileSync(addFile.name, base64File.toString('base64url'), { encoding: 'base64url' });
                    const file = fs.readFileSync(addFile.name, { encoding: 'base64' });

                    const fileData = new FormData();
                    fileData.append(addFile.name, file);

                    await axios
                        .post(`${url}/assignments/${assignmentId}/upload`, fileData, config)
                        .catch(async (err) => {
                            logger.error(err.stack);
                        });
                }

                if (removeFile) {
                    await axios
                        .delete(`${url}/assignments/${assignmentId}/remove-file`, config)
                        .catch(async (err) => {
                            await interaction.reply('Error removing file.');
                            logger.error(err.stack);
                        });
                }

                await interaction.reply('Homework assignment edited!');

            } else if (subcommand === 'peer-review') {
                if (role !== 'professor') {
                    await interaction.reply('You are not authorized to edit peer review assignments.');
                    return;
                }

                const name = interaction.options.getString('name');
                const instructions = interaction.options.getString('instructions');
                const dueDate = interaction.options.getString('due-date');
                const points = interaction.options.getInteger('points');
                const addRubric = interaction.options.getAttachment('add-rubric');
                const addTemplate = interaction.options.getAttachment('add-template');
                const removeRubric = interaction.options.getString('remove-rubric');
                const removeTemplate = interaction.options.getString('remove-template');

                const url = `http://professor-assignment:13130/assignments/professor/courses/${courseId}`;
                const assignment = await axios
                    .get(`${url}/assignments`, config)
                    .then((res) => {
                        return res.data.find(assignment => assignment.assignment_name === name);
                    })
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                const assignmentId = assignment.assignment_id;
                const data = {
                    course_id: courseId,
                    assignment_name: name,
                    instructions: assignment.instructions,
                    due_date: assignment.due_date,
                    points: assignment.points,
                    peer_review_instructions: assignment.peer_review_instructions,
                    peer_review_due_date: assignment.peer_review_due_date,
                    peer_review_points: assignment.peer_review_points
                };

                if (instructions) data.peer_review_instructions = instructions;
                if (dueDate) data.peer_review_due_date = dueDate;
                if (points) data.peer_review_points = points;

                await axios
                    .put(`${url}/assignments/${assignmentId}/edit`, data, config)
                    .catch(async (err) => {
                        await interaction.reply('Error editing peer review.');
                        logger.error(err.stack);
                    });

                if (addRubric) {
                    const base64File = await axios
                        .get(addRubric.url, { responseType: 'arraybuffer', responseEncoding: 'base64url' })
                        .then((res) => {
                            let temp = Buffer.from(res.data, 'base64url').toString('base64url');
                            temp = temp.replace('data:', '').replace(/^.+,/, '');
                            return Buffer.from(temp, 'base64url');
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    fs.writeFileSync(addRubric.name, base64File.toString('base64url'), { encoding: 'base64url' });
                    const file = fs.readFileSync(addRubric.name, { encoding: 'base64' });

                    const rubricData = new FormData();
                    rubricData.append(addRubric.name, file);

                    await axios
                        .post(`${url}/assignments/${assignmentId}/peer-review/rubric/upload`, rubricData, config)
                        .catch(async (err) => {
                            logger.error(err.stack);
                        });
                }

                if (addTemplate) {
                    const base64File = await axios
                        .get(addTemplate.url, { responseType: 'arraybuffer', responseEncoding: 'base64url' })
                        .then((res) => {
                            let temp = Buffer.from(res.data, 'base64url').toString('base64url');
                            temp = temp.replace('data:', '').replace(/^.+,/, '');
                            return Buffer.from(temp, 'base64url');
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    fs.writeFileSync(addTemplate.name, base64File.toString('base64url'), { encoding: 'base64url' });
                    const file = fs.readFileSync(addTemplate.name, { encoding: 'base64' });

                    const templateData = new FormData();
                    templateData.append(addTemplate.name, file);

                    await axios
                        .post(`${url}/assignments/${assignmentId}/peer-review/template/upload`, templateData, config)
                        .catch(async (err) => {
                            logger.error(err.stack);
                        });
                }

                if (removeRubric) {
                    await axios
                        .delete(`${url}/assignments/${assignmentId}/peer-review/rubric/remove-file`, config)
                        .catch(async (err) => {
                            await interaction.reply('Error removing rubric file.');
                            logger.error(err.stack);
                        });
                }

                if (removeTemplate) {
                    await axios
                        .delete(`${url}/assignments/${assignmentId}/peer-review/template/remove-file`, config)
                        .catch(async (err) => {
                            await interaction.reply('Error removing template file.');
                            logger.error(err.stack);
                        });
                }

                await interaction.reply('Peer review assignment created!');

            } else if (subcommand === 'team') {
                if (role !== 'professor') {
                    await interaction.reply('You are not authorized to edit teams.');
                    return;
                }

                const name = interaction.options.getString('name');
                const newName = interaction.options.getString('new-name');
                const addStudent = interaction.options.getMember('add-student');
                const removeStudent = interaction.options.getMember('remove-student');

                const url = 'http://peer-review-teams:13129/teams/professor/team';
                const team = await axios
                    .get(`${url}/get/all/${courseId}`, config)
                    .then((res) => {
                        return res.data.find(team => team.team_name === name);
                    })
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                const data = {
                    course_id: courseId,
                    nominated_team_leader: team.team_lead,
                    team_id: team.team_id,
                    team_name: team.team_id,
                    team_size: team.team_size,
                    student_id: interaction.user.id
                };

                if (newName) {
                    if (newName.split(' ').length > 1) {
                        await interaction.reply('Please enter a team name with no spaces.');
                        return;
                    }

                    if (newName === '') {
                        await interaction.reply('Team name cannot be empty.');
                        return;
                    }

                    if (newName.length > 20) {
                        await interaction.reply('Team name is too long.');
                        return;
                    }

                    data.team_name = newName;

                    await axios
                        .put(`${url}/team-name/edit`, data, config)
                        .catch(async (err) => {
                            await interaction.reply('Error editing team.');
                            logger.error(err.stack);
                        });
                }

                if (addStudent) {
                    data.student_id = addStudent.displayName;

                    await axios
                        .put(`${url}/add-student`, data, config)
                        .catch(async (err) => {
                            await interaction.reply('Error adding student to team.');
                            logger.error(err.stack);
                        });
                }

                if (removeStudent) {
                    data.student_id = removeStudent.displayName;

                    await axios
                        .put(`${url}/remove-student`, data, config)
                        .catch(async (err) => {
                            await interaction.reply('Error removing student from team.');
                            logger.error(err.stack);
                        });
                }

                await interaction.reply('Team created!');

            } else if (subcommand === 'course') {
                if (role !== 'professor') {
                    await interaction.reply('You are not authorized to edit courses.');
                    return;
                }

                const addStudent = interaction.options.getString('add-student');
                const removeStudent = interaction.options.getMember('remove-student');

                const url = `http://course-manager:13127/manage/professor/courses/${courseId}/students`;

                if (addStudent) {
                    const data = addStudent.split(' ');

                    if (data.length < 3) {
                        await interaction.reply('Please enter first, last, and email for the student.');
                        return;
                    }

                    if (!data[2].includes('oswego.edu')) {
                        await interaction.reply('Please enter a valid Oswego email.');
                        return;
                    }

                    await axios
                        .post(`${url}/${addStudent}/add`, null, config)
                        .catch(async (err) => {
                            await interaction.reply('Error adding student to course.');
                            logger.error(err.stack);
                        });
                }

                if (removeStudent) {
                    await axios
                        .post(`${url}/${removeStudent.displayName}/delete`, null, config)
                        .catch(async (err) => {
                            await interaction.reply('Error removing student from course.');
                            logger.error(err.stack);
                        });
                }

                await interaction.reply('Course edited!');
            }

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
