const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger, authenticate } = require('../utils');
const axios = require('axios');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('join')
        .setDescription('Join a team')
        .addStringOption(option => option
            .setName('team')
            .setDescription('The name of the team to join')
            .setRequired(true)),

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {
            const teamId = interaction.options.getString('name');
            const { headers, courseId } = await authenticate(interaction)
                .catch(async (err) => {
                    await interaction.reply({ content: err.message, ephemeral: true });
                    logger.error('User not logged in');
                });

            const data = {
                team_id: teamId,
                course_id: courseId,
                student_id: interaction.member.displayName
            };

            await axios
                .put('http://peer-review-teams:13129/teams/team/join', data, headers)
                .catch(async (err) => {
                    await interaction.reply({ content: 'Error joining team!', ephemeral: true });
                    logger.error(err.stack);
                });

            await interaction.reply('Team joined!');

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
