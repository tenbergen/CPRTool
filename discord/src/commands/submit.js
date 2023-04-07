const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger, authenticate} = require('../utils');
const axios = require("axios");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('submit')
        .setDescription('Submits a solution or peer review')
        .addSubcommand(subcommand => subcommand
            .setName('homework')
            .setDescription('Submits a homework assignment')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the homework assignment')
                .setRequired(true))
            .addAttachmentOption(option => option
                .setName('file')
                .setDescription('The file to upload for the homework assignment')
                .setRequired(true)))
        .addSubcommand(subcommand => subcommand
            .setName('peer-review')
            .setDescription('Submits a peer review')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the associated homework assignment')
                .setRequired(true))
            .addStringOption(option => option
                .setName('team-name')
                .setDescription('The name of the team that being reviewed')
                .setRequired(true))
            .addIntegerOption(option => option
                .setName('grade')
                .setDescription('The grade to give the reviewed team')
                .setRequired(true))
            .addAttachmentOption(option => option
                .setName('file')
                .setDescription('The file to upload for the peer review')
                .setRequired(true))),

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {
            const subcommand = interaction.options.getSubcommand();
            const file = interaction.options.getAttachment('file');
            const { headers, roles, courseId } = await authenticate(interaction)
                .catch(async (err) => {
                    await interaction.reply({ content: err.message, ephemeral: true });
                    logger.error('User not logged in');
                });

            if (subcommand === 'homework') {
                const assignmentName = interaction.options.getString('name');

                const url = `http://student-assignment:13121/assignments/student/courses/${courseId}`;
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

                const assignmentId = await axios
                    .get(`http://professor-assignment:13130/assignments/professor/courses/${courseId}/assignments`, headers)
                    .then((res) => {
                        return res.data.find(assignment => assignment.assignment_name === name).assignment_id;
                    })
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                const teamId = await axios
                    .get(`http://peer-review-teams:13129/teams/team/get/all/${courseId}`, headers)
                    .then((res) => {
                        return res.data.find(team => team.team_members.includes(interaction.member.displayName)).team_id;
                    })
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                await axios
                    .post(`${url}/assignments/${assignmentId}/${teamId}/upload`, fileData, headers)
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                await interaction.reply('Submission successful!');

            } else if (subcommand === "peer-review") {
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

                const grade = interaction.options.getInteger('grade');
                const otherTeamId = interaction.options.getString('team-name');
                const teamId = await axios
                    .get(`http://peer-review-teams:13129/teams/team/get/all/${courseId}`, headers)
                    .then((res) => {
                        return res.data.find(team => team.team_members.includes(interaction.member.displayName)).team_id;
                    })
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                const name = interaction.options.getString('name');
                const url = "http://student-assignment:13121/assignments/student";

                const assignmentId = await axios
                    .get(`http://professor-assignment:13130/assignments/professor/courses/${courseId}/assignments`, headers)
                    .then((res) => {
                        return res.data.find(assignment => assignment.assignment_name === name).assignment_id;
                    })
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                await axios
                    .post(`${url}/${courseId}/${assignmentId}/${teamId}/${otherTeamId}/${grade}/upload`, fileData, headers)
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                await interaction.reply('Submission successful!');
            }

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
