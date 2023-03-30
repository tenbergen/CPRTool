const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('list')
        .setDescription('Lists all your scores, assignments, solutions, students, or teams')
        .addSubcommand(subcommand => subcommand
            .setName('scores')
            .setDescription('Lists all your scores for the class'))
        .addSubcommand(subcommand => subcommand
            .setName('assignments')
            .setDescription('Lists all your assignments for the class'))
        .addSubcommand(subcommand => subcommand
            .setName('solutions')
            .setDescription('Lists all your solutions for the class'))
        .addSubcommand(subcommand => subcommand
            .setName('students')
            .setDescription('Lists all the students in the class'))
        .addSubcommand(subcommand => subcommand
            .setName('teams')
            .setDescription('Lists all the teams in the class')),

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
