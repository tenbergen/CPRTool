const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');

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

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
