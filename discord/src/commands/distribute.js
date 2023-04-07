const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger, authenticate } = require('../utils');
const axios = require('axios');

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
            const name = interaction.options.getString('name');
            const number = interaction.options.getString('number');
            const { headers, roles, courseId } = await authenticate(interaction)
                .catch(async (err) => {
                    await interaction.reply({ content: err.message, ephemeral: true });
                    logger.error('User not logged in');
                });

            if (!roles.includes('professor')) {
                await interaction.reply({ content: 'You are not authorized to distribute peer reviews!', ephemeral: true });
                return;
            }

            const assignmentId = await axios
                .get(`http://professor-assignment:13130/assignments/professor/courses/${courseId}/assignments`, headers)
                .then((res) => {
                    return res.data.find(assignment => assignment.assignment_name === name).assignment_id;
                })
                .catch((err) => {
                    logger.error(err.stack);
                });

            const url = `http://student-peer-review-assignment:13132/peer-review/assignments/${courseId}/${assignmentId}`;

            await axios
                .get(`${url}/assign/${number}`, headers)
                .catch(async (err) => {
                    await interaction.reply({ content: 'Error distributing peer reviews!', ephemeral: true });
                    logger.error(err.stack);
                });

            await interaction.reply('Peer reviews distributed!');

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
