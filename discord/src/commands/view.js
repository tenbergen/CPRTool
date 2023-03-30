const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('view')
        .setDescription('Views an assignment or team')
        .addSubcommand(subcommand => subcommand
            .setName('assignment')
            .setDescription('Views an assignment')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the assignment')
                .setRequired(true)))
        .addSubcommand(subcommand => subcommand
            .setName('team')
            .setDescription('Lists the members of a team')),

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
