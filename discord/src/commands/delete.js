const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger, authenticate } = require('../utils');
const axios = require('axios');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('delete')
        .setDescription('Deletes an assignment or team')
        .addSubcommand(subcommand => subcommand
            .setName('assignment')
            .setDescription('Deletes an assignment')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the assignment to delete')
                .setRequired(true)))
        .addSubcommand(subcommand => subcommand
            .setName('team')
            .setDescription('Deletes a team')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the team to delete')
                .setRequired(true))),

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {
            const subcommand = interaction.options.getSubcommand();
            const name = interaction.options.getString('name');
            const { headers, roles, courseId } = await authenticate(interaction)
                .catch(async (err) => {
                    await interaction.reply({ content: err.message, ephemeral: true });
                    logger.error('User not logged in');
                });

            if (subcommand === 'assignment') {
                if (!roles.includes('professor')) {
                    await interaction.reply({ content: 'You are not authorized to delete assignments!', ephemeral: true });
                    return;
                }

                const url = `http://professor-assignment:13130/assignments/professor/courses/${courseId}`;
                const assignmentId = await axios
                    .get(`${url}/assignments/`, headers)
                    .then((res) => {
                        return res.data.find(assignment => assignment.assignment_name === name).assignment_id;
                    })
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                await axios
                    .delete(`${url}/assignments/${assignmentId}/remove`, headers)
                    .catch(async (err) => {
                        await interaction.reply({ content: 'Error deleting assignment!', ephemeral: true });
                        logger.error(err.stack);
                    });

                await interaction.reply('Assignment deleted!');

            } else if (subcommand === 'team') {
                if (!roles.includes('professor')) {
                    await interaction.reply({ content: 'You are not authorized to delete teams!', ephemeral: true });
                    return;
                }

                const url = 'http://peer-review-teams:13129/teams/profebackendssor/team';
                const newHeaders = headers;
                newHeaders.data = {
                    team_id: name,
                    course_id: courseId,
                };

                await axios
                    .delete(`${url}/delete`, newHeaders)
                    .catch(async (err) => {
                        await interaction.reply({ content: 'Error deleting team!', ephemeral: true });
                        logger.error(err.stack);
                    });

                await interaction.reply('Team deleted!');
            }

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
