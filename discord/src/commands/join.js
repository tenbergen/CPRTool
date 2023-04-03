const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');
const axios = require("axios");
const decode = require("jwt-decode");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('join')
        .setDescription('Join a team')
        .addStringOption(option => option
            .setName('team')
            .setDescription('The name of the team to join')
            .setRequired(true)),

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

            const teamId = interaction.options.getString('name');

            const data = {
                team_id: teamId,
                course_id: courseId,
                student_id: interaction.member.displayName
            };

            await axios
                .put('http://peer-review-teams:13129/teams/team/join', data, config)
                .catch(async (err) => {
                    await interaction.reply("Error joining team.");
                    logger.error(err.stack);
                });

            await interaction.reply('Team joined!');

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
