const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const decode = require("jwt-decode");
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
            const token = interaction.client.accounts.get(interaction.user.id);
            if (!token) {
                const commands = await interaction.guild.commands.fetch();
                const command = commands.find(cmd => cmd.name === 'login');

                await interaction.reply(`You are not logged in. Please login with </login:${command.id}>.`, { ephemeral: true });
                return;
            }

            const role = decode(token).groups[0];
            const config = {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            };

            const courseId = await axios
                .get('http://course-viewer:13128/view/professor/courses', config)
                .then((res) => {
                    return res.data.find(course => course.course_name === interaction.guild.name).course_id;
                })
                .catch((err) => {
                    logger.error(err.stack);
                });

            const subcommand = interaction.options.getSubcommand();
            const name = interaction.options.getString('name');

            if (subcommand === 'assignment') {
                if (role !== 'professor') {
                    await interaction.reply('You are not authorized to delete assignments.');
                    return;
                }

                const url = `http://professor-assignment:13130/assignments/professor/courses/${courseId}`;
                const assignmentId = await axios
                    .get(`${url}/assignments/`, config)
                    .then((res) => {
                        return res.data.find(assignment => assignment.assignment_name === name).assignment_id;
                    })
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                await axios
                    .delete(`${url}/assignments/${assignmentId}/remove`, config)
                    .catch(async (err) => {
                        await interaction.reply("Error deleting assignment.");
                        logger.error(err.stack);
                    });

                await interaction.reply('Assignment deleted!');

            } else if (subcommand === 'team') {
                if (role !== 'professor') {
                    await interaction.reply('You are not authorized to delete teams.');
                    return;
                }

                const url = 'http://peer-review-teams:13129/teams/professor/team';
                config.data = {
                    team_id: name,
                    course_id: courseId,
                };

                await axios
                    .delete(`${url}/delete`, config)
                    .catch(async (err) => {
                        await interaction.reply("Error deleting team.");
                        logger.error(err.stack);
                    });

                await interaction.reply('Team deleted!');
            }

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
