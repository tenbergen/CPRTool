const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const decode = require("jwt-decode");
const { logger } = require('../utils');
const axios = require("axios");

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

            const name = interaction.options.getString('name');
            const number = interaction.options.getString('number');

            if (role !== 'professor') {
                await interaction.reply('You are not authorized to distribute peer reviews.');
                return;
            }

            const assignmentId = await axios
                .get(`http://professor-assignment:13130/assignments/professor/courses/${courseId}/assignments`, config)
                .then((res) => {
                    return res.data.find(assignment => assignment.assignment_name === name).assignment_id;
                })
                .catch((err) => {
                    logger.error(err.stack);
                });

            const url = `http://student-peer-review-assignment:13132/peer-review/assignments/${courseId}/${assignmentId}`;

            await axios
                .get(`${url}/assign/${number}`, config)
                .catch(async (err) => {
                    await interaction.reply("Error distributing peer reviews.");
                    logger.error(err.stack);
                });

            await interaction.reply('Peer reviews distributed!');

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
