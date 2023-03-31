const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');
const axios = require('axios');

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
            const subcommand = interaction.options.getSubcommand();
            const name = interaction.options.getString('name');
            const courseId = interaction.guild.name;

            if (subcommand === 'assignment') {
                const url = `${process.env.REACT_APP_URL}/assignments/professor/courses/${courseId}`;

                const assignmentId = await axios
                    .get(`${url}/assignments/`)
                    .then((res) => {
                        return res.data.find(assignment => assignment.assignment_name === name).assignment_id
                    });

                await axios.delete(`${url}/assignments/${assignmentId}/remove`);
                await interaction.reply('Assignment deleted!');

            } else if (subcommand === 'team') {
                const url = `${process.env.REACT_APP_URL}/teams/professor/team`;

                const data = {
                    team_id: name,
                    course_id: courseId,
                };

                await axios
                    .delete(`${url}/delete`, { data })
                    .catch(async (err) => {
                        await interaction.reply("Error removing team.");
                        logger.error(err.stack);
                    });

                await interaction.reply('Team deleted!');
            }

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
