const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');
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
                .setName('due')
                .setDescription('The due date for the homework assignment')
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
            .setDescription('Creates a peer review for an assignment')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the assignment to associate with')
                .setRequired(true))
            .addStringOption(option => option
                .setName('instructions')
                .setDescription('The instructions for the peer review')
                .setRequired(true))
            .addStringOption(option => option
                .setName('due')
                .setDescription('The due date for the peer review')
                .setRequired(true))
            .addIntegerOption(option => option
                .setName('points')
                .setDescription('The total points for the peer review')
                .setRequired(true))
            .addAttachmentOption(option => option
                .setName('rubric')
                .setDescription('The rubric to upload for the peer review'))
            .addAttachmentOption(option => option
                .setName('template')
                .setDescription('The template to upload for the peer review')))
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
            const courseId = interaction.guild.name; // TODO: make courseId come from course name

            if (subcommand === 'homework') {
                const name = interaction.options.getString('name');
                const instructions = interaction.options.getString('instructions');
                const due = interaction.options.getString('due');
                const points = interaction.options.getInteger('points');
                const file = interaction.options.getAttachment('file');

                // TODO: figure out where react app url comes from
                const url = `${process.env.REACT_APP_URL}/assignments/professor/courses`;
                const data = {
                    course_id: courseId,
                    assignment_name: name,
                    instructions: instructions,
                    due_date: due,
                    points: points
                };

                await axios.post(`${url}/create-assignment-no-peer-review`, data);

                if (file) {
                    const assignmentId = await axios
                        .get(`${url}/${courseId}/assignments`)
                        .then(async (res) => {
                            return res.data.find(assignment => assignment.assignment_name === name).assignment_id;
                        });

                    const fileBlob = await axios
                        .get(file.url, { responseType: 'blob' })
                        .then(async (res) => {
                            return new Blob([res.data], { type: file.contentType });
                        });

                    const fileData = new FormData();
                    fileData.append(file.name, fileBlob);

                    await axios
                        .post(`${url}/${courseId}/assignments/${assignmentId}/upload`, fileData)
                        .catch(async (err) => {
                            await interaction.reply('Error uploading assignment file.');
                            logger.error(err.stack);
                        });
                }

                await interaction.reply('Homework assignment created.');

            } else if (subcommand === 'peer-review') {
                const name = interaction.options.getString('name');
                const instructions = interaction.options.getString('instructions');
                const due = interaction.options.getString('due');
                const points = interaction.options.getInteger('points');
                const rubric = interaction.options.getAttachment('rubric');
                const template = interaction.options.getAttachment('template');

                const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}`;
                const data = {
                    instructions: instructions,
                    due_date: due,
                    points: points
                };

                const assignmentId = await axios
                    .get(`${url}/assignments`)
                    .then((res) => {
                        return res.data.find(assignment => assignment.assignment_name === name).assignment_id;
                    });

                await axios.post(`${url}/assignments/${assignmentId}/addPeerReviewData`, data);

                if (rubric) {
                    const rubricBlob = await axios
                        .get(rubric.url, { responseType: 'blob' })
                        .then((res) => {
                            return new Blob([res.data], { type: rubric.contentType });
                        });

                    const rubricData = new FormData();
                    rubricData.append(rubric.name, rubricBlob);

                    await axios
                        .post(`${url}/assignments/${assignmentId}/peer-review/rubric/upload`, rubricData)
                        .catch(async (err) => {
                            await interaction.reply('Error uploading rubric file.');
                            logger.error(err.stack);
                        });
                }

                if (template) {
                    const templateBlob = await axios
                        .get(template.url, { responseType: 'blob' })
                        .then((res) => {
                            return new Blob([res.data], { type: template.contentType });
                        });

                    const templateData = new FormData();
                    templateData.append(template.name, templateBlob);

                    await axios
                        .post(`${url}/assignments/${assignmentId}/peer-review/template/upload`, templateData)
                        .catch(async (err) => {
                            await interaction.reply('Error uploading template file.');
                            logger.error(err.stack);
                        });
                }

                await interaction.reply('Peer review assignment created.');

            } else if (subcommand === 'team') {
                const name = interaction.options.getString('name');

                if (name.split(' ').length > 1) {
                    await interaction.reply('Please enter a team name with no spaces!');
                    return;
                }

                if (name === '') {
                    await interaction.reply('Team name cannot be empty!');
                    return;
                }

                if (name.length > 20){
                    await interaction.reply('Team name is too long!');
                    return;
                }

                const data = {
                    course_id: courseId,
                    student_id: interaction.member.displayName,
                    team_name: name
                };

                await axios.post(`${process.env.REACT_APP_URL}/teams/team/create`, data);
                await interaction.reply('Team created.');
            }

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
