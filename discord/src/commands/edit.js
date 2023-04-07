const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger, authenticate } = require('../utils');
const axios = require('axios');

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
            const subcommand = interaction.options.getSubcommand();
            const { headers, roles, courseId } = await authenticate(interaction)
                .catch(async (err) => {
                    await interaction.reply({ content: err.message, ephemeral: true });
                    logger.error('User not logged in');
                });

            if (subcommand === 'homework') {
                const name = interaction.options.getString('name');
                const newName = interaction.options.getString('new-name');
                const instructions = interaction.options.getString('instructions');
                const dueDate = interaction.options.getString('due-date');
                const points = interaction.options.getInteger('points');
                const addFile = interaction.options.getAttachment('add-file');
                const removeFile = interaction.options.getString('remove-file');

                if (!roles.includes('professor')) {
                    await interaction.reply({ content: 'You are not authorized to edit homework assignments!', ephemeral: true });
                    return;
                }

                const url = `http://professor-assignment:13130/assignments/professor/courses/${courseId}`;
                const assignment = await axios
                    .get(`${url}/assignments`, headers)
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
                    .put(`${url}/assignments/${assignmentId}/editNoPeerReview`, data, headers)
                    .catch(async (err) => {
                        await interaction.reply({ content: 'Error editing homework assignment!', ephemeral: true });
                        logger.error(err.stack);
                    });

                if (addFile) {
                    const base64File = await axios
                        .get(addFile.url, { responseType: 'arraybuffer', responseEncoding: 'base64' })
                        .then((res) => {
                            return Buffer.from(res.data, 'base64').toString('base64');
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    const fileData = new FormData();
                    fileData.append(addFile.name, base64File);

                    await axios
                        .post(`${url}/assignments/${assignmentId}/upload`, fileData, headers)
                        .catch((err) => {
                            logger.error(err.stack);
                        });
                }

                if (removeFile) {
                    await axios
                        .delete(`${url}/assignments/${assignmentId}/remove-file`, headers)
                        .catch(async (err) => {
                            await interaction.reply({ content: 'Error removing file!', ephemeral: true });
                            logger.error(err.stack);
                        });
                }

                await interaction.reply('Homework assignment edited!');

            } else if (subcommand === 'peer-review') {
                const name = interaction.options.getString('name');
                const instructions = interaction.options.getString('instructions');
                const dueDate = interaction.options.getString('due-date');
                const points = interaction.options.getInteger('points');
                const addRubric = interaction.options.getAttachment('add-rubric');
                const addTemplate = interaction.options.getAttachment('add-template');
                const removeRubric = interaction.options.getString('remove-rubric');
                const removeTemplate = interaction.options.getString('remove-template');

                if (!roles.includes('professor')) {
                    await interaction.reply({ content: 'You are not authorized to edit peer review assignments!', ephemeral: true });
                    return;
                }

                const url = `http://professor-assignment:13130/assignments/professor/courses/${courseId}`;
                const assignment = await axios
                    .get(`${url}/assignments`, headers)
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
                    .put(`${url}/assignments/${assignmentId}/edit`, data, headers)
                    .catch(async (err) => {
                        await interaction.reply({ content: 'Error editing peer review!', ephemeral: true });
                        logger.error(err.stack);
                    });

                if (addRubric) {
                    const base64File = await axios
                        .get(addRubric.url, { responseType: 'arraybuffer', responseEncoding: 'base64' })
                        .then((res) => {
                            return Buffer.from(res.data, 'base64').toString('base64');
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    const rubricData = new FormData();
                    rubricData.append(addRubric.name, base64File);

                    await axios
                        .post(`${url}/assignments/${assignmentId}/peer-review/rubric/upload`, rubricData, headers)
                        .catch((err) => {
                            logger.error(err.stack);
                        });
                }

                if (addTemplate) {
                    const base64File = await axios
                        .get(addTemplate.url, { responseType: 'arraybuffer', responseEncoding: 'base64' })
                        .then((res) => {
                            return Buffer.from(res.data, 'base64').toString('base64');
                        })
                        .catch((err) => {
                            logger.error(err.stack);
                        });

                    const templateData = new FormData();
                    templateData.append(addTemplate.name, base64File);

                    await axios
                        .post(`${url}/assignments/${assignmentId}/peer-review/template/upload`, templateData, headers)
                        .catch((err) => {
                            logger.error(err.stack);
                        });
                }

                if (removeRubric) {
                    await axios
                        .delete(`${url}/assignments/${assignmentId}/peer-review/rubric/remove-file`, headers)
                        .catch(async (err) => {
                            await interaction.reply({ content: 'Error removing rubric file!', ephemeral: true });
                            logger.error(err.stack);
                        });
                }

                if (removeTemplate) {
                    await axios
                        .delete(`${url}/assignments/${assignmentId}/peer-review/template/remove-file`, headers)
                        .catch(async (err) => {
                            await interaction.reply({ content: 'Error removing template file!', ephemeral: true });
                            logger.error(err.stack);
                        });
                }

                await interaction.reply('Peer review assignment created!');

            } else if (subcommand === 'team') {
                const name = interaction.options.getString('name');
                const newName = interaction.options.getString('new-name');
                const addStudent = interaction.options.getMember('add-student');
                const removeStudent = interaction.options.getMember('remove-student');

                if (!roles.includes('professor')) {
                    await interaction.reply({ content: 'You are not authorized to edit teams!', ephemeral: true });
                    return;
                }

                const url = 'http://peer-review-teams:13129/teams/professor/team';
                const team = await axios
                    .get(`${url}/get/all/${courseId}`, headers)
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
                        await interaction.reply({ content: 'Please enter a team name with no spaces!', ephemeral: true });
                        return;
                    }

                    if (newName === '') {
                        await interaction.reply({ content: 'Team name cannot be empty!', ephemeral: true });
                        return;
                    }

                    if (newName.length > 20) {
                        await interaction.reply({ content: 'Team name is too long!', ephemeral: true });
                        return;
                    }

                    data.team_name = newName;

                    await axios
                        .put(`${url}/team-name/edit`, data, headers)
                        .catch(async (err) => {
                            await interaction.reply({ content: 'Error editing team!', ephemeral: true });
                            logger.error(err.stack);
                        });
                }

                if (addStudent) {
                    data.student_id = addStudent.displayName;

                    await axios
                        .put(`${url}/add-student`, data, headers)
                        .catch(async (err) => {
                            await interaction.reply({ content: 'Error adding student to team!', ephemeral: true });
                            logger.error(err.stack);
                        });
                }

                if (removeStudent) {
                    data.student_id = removeStudent.displayName;

                    await axios
                        .put(`${url}/remove-student`, data, headers)
                        .catch(async (err) => {
                            await interaction.reply({ content: 'Error removing student from team!', ephemeral: true });
                            logger.error(err.stack);
                        });
                }

                await interaction.reply('Team created!');

            } else if (subcommand === 'course') {
                const addStudent = interaction.options.getString('add-student');
                const removeStudent = interaction.options.getMember('remove-student');

                if (!roles.includes('professor')) {
                    await interaction.reply({ content: 'You are not authorized to edit courses!', ephemeral: true });
                    return;
                }

                const url = `http://course-manager:13127/manage/professor/courses/${courseId}/students`;

                if (addStudent) {
                    const data = addStudent.split(' ');

                    if (data.length < 3) {
                        await interaction.reply({ content: 'Please enter first, last, and email for the student!', ephemeral: true });
                        return;
                    }

                    if (!data[2].includes('oswego.edu')) {
                        await interaction.reply({ content: 'Please enter a valid Oswego email!', ephemeral: true });
                        return;
                    }

                    await axios
                        .post(`${url}/${data[0]}-${data[1]}-${data[2]}/add`, {}, headers)
                        .catch(async (err) => {
                            await interaction.reply({ content: 'Error adding student to course!', ephemeral: true });
                            logger.error(err.stack);
                        });
                }

                if (removeStudent) {
                    await axios
                        .post(`${url}/${removeStudent.displayName}/delete`, null, headers)
                        .catch(async (err) => {
                            await interaction.reply({ content: 'Error removing student from course!', ephemeral: true });
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
