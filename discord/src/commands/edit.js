const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('edit')
        .setDescription('Edits an assignment or peer review or team')
        .addSubcommand(subcommand => subcommand
            .setName('homework')
            .setDescription('Edits a homework assignment')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the homework assignment to edit')
                .setRequired(true))
            .addStringOption(option => option
                .setName('new-name')
                .setDescription('The new name for the homework assignment'))
            .addStringOption(option => option
                .setName('instructions')
                .setDescription('The new instructions for the homework assignment'))
            .addStringOption(option => option
                .setName('due')
                .setDescription('The new due date for the homework assignment'))
            .addIntegerOption(option => option
                .setName('points')
                .setDescription('The new total points for the homework assignment'))
            .addAttachmentOption(option => option
                .setName('add-file')
                .setDescription('The file to upload for the homework assignment'))
            .addStringOption(option => option
                .setName('remove-file')
                .setDescription('The file to remove from the homework assignment')))
        .addSubcommand(subcommand => subcommand
            .setName('peer-review')
            .setDescription('Edits a peer review')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the associated assignment')
                .setRequired(true))
            .addStringOption(option => option
                .setName('instructions')
                .setDescription('The new instructions for the peer review'))
            .addStringOption(option => option
                .setName('due')
                .setDescription('The new due date for the peer review'))
            .addIntegerOption(option => option
                .setName('points')
                .setDescription('The new total points for the peer review'))
            .addAttachmentOption(option => option
                .setName('add-rubric')
                .setDescription('The rubric to upload for the peer review'))
            .addAttachmentOption(option => option
                .setName('add-template')
                .setDescription('The template to upload for the peer review'))
            .addStringOption(option => option
                .setName('remove-rubric')
                .setDescription('The rubric to remove from the peer review'))
            .addStringOption(option => option
                .setName('remove-template')
                .setDescription('The template to remove from the peer review')))
        .addSubcommand(subcommand => subcommand
            .setName('team')
            .setDescription('Edits a team')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the team to edit')
                .setRequired(true))
            .addStringOption(option => option
                .setName('new-name')
                .setDescription('The new name for the team'))
            .addStringOption(option => option
                .setName('add-student')
                .setDescription('The name of the student to add'))
            .addStringOption(option => option
                .setName('remove-student')
                .setDescription('The name of the student to remove')))
        .addSubcommand(subcommand => subcommand
            .setName('course')
            .setDescription('Edits a course')
            .addStringOption(option => option
                .setName('add-student')
                .setDescription('The name of the student to add'))
            .addStringOption(option => option
                .setName('remove-student')
                .setDescription('The name of the student to remove'))),

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
