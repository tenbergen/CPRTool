const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('submit')
        .setDescription('Submits a solution or peer review')
        .addSubcommand(subcommand => subcommand
            .setName('homework')
            .setDescription('Submits a homework assignment')
            .addAttachmentOption(option => option
                .setName('file')
                .setDescription('The file to upload for the homework assignment')
                .setRequired(true)))
        .addSubcommand(subcommand => subcommand
            .setName('peer-review')
            .setDescription('Submits a peer review')
            .addAttachmentOption(option => option
                .setName('file')
                .setDescription('The file to upload for the peer review')
                .setRequired(true))),

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
