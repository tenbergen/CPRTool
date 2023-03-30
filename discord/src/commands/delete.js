const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');

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

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
