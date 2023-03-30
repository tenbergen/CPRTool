const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('distribute')
        .setDescription('Distributes a peer review')
        .addStringOption(option => option
            .setName('name')
            .setDescription('The name of the assignment to distribute')
            .setRequired(true))
        .addStringOption(option => option
            .setName('number')
            .setDescription('The number of reviews to distribute per team')
            .setRequired(true)),

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
