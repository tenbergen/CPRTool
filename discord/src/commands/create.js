const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');

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
                .setDescription('The file to upload for the homework assignment')
                .setRequired(true)))
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
                .setDescription('The rubric to upload for the peer review')
                .setRequired(true))
            .addAttachmentOption(option => option
                .setName('template')
                .setDescription('The template to upload for the peer review')
                .setRequired(true)))
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

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
