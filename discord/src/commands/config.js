const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('config')
        .setDescription('Configures the bot for your class')
        .addStringOption(option => option
            .setName('class')
            .setDescription('The name of the class')
            .setRequired(true))
        .addStringOption(option => option
            .setName('instructor')
            .setDescription('The name of the instructor')
            .setRequired(true)),

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
