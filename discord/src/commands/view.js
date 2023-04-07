const { SlashCommandBuilder, ChatInputCommandInteraction, EmbedBuilder, AttachmentBuilder } = require('discord.js');
const { logger, authenticate } = require('../utils');
const axios = require('axios');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('view')
        .setDescription('Views an assignment or team')
        .addSubcommand(subcommand => subcommand
            .setName('assignment')
            .setDescription('Views an assignment')
            .addStringOption(option => option
                .setName('name')
                .setDescription('The name of the assignment')
                .setRequired(true)))
        .addSubcommand(subcommand => subcommand
            .setName('team')
            .setDescription('Lists the members of your team')),

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {
            const subcommand = interaction.options.getSubcommand();
            const { headers, roles, courseId } = await authenticate(interaction)
                .catch(async (err) => {
                    await interaction.reply({ content: err.message, ephemeral: true });
                    logger.error('User not logged in');
                });

            if (subcommand === 'assignment') {
                const name = interaction.options.getString('name');

                const url = `http://professor-assignment:13130/assignments/professor/courses/${courseId}`;
                const assignment = await axios
                    .get(`${url}/assignments`, headers)
                    .then(res => {
                        return res.data.find(assignment => assignment.assignment_name === name);
                    })
                    .catch(async (err) => {
                        await interaction.reply({ content: "Error retrieving assignment.", ephemeral: true });
                        logger.error(err.stack);
                    });

                const files = [];
                const embed = new EmbedBuilder()
                    .setTitle(assignment.assignment_name)
                    .setDescription(assignment.instructions)
                    .addFields({ name: 'Due Date', value: assignment.due_date });

                if (assignment.rubric_data) {
                    const attachment = new AttachmentBuilder(Buffer.from(assignment.rubric_data, 'base64'), {name: 'rubric.pdf'});
                    files.push(attachment);
                }

                await interaction.reply({ embeds: [embed], files: files, ephemeral: true });

            } else if (subcommand === "team") {
                const name = interaction.options.getString("team");

                const url = 'http://peer-review-teams:13129/teams/team';
                const team = await axios
                    .get(`${url}/get/all/${courseId}`, headers)
                    .then((res) => {
                        return res.data.find(team => team.team_members.includes(interaction.member.displayName));
                    })
                    .catch(async (err) => {
                        await interaction.reply({ content: 'Error getting teams.', ephemeral: true });
                        logger.error(err.stack);
                    });

                let counter = 1;
                let teamNames = "";
                const embed = new EmbedBuilder()
                    .setTitle(`${name}'s Members`);

                for (const member of team) {
                    teamNames += `${counter}. ${member}\n`;
                    counter++;
                }

                embed.setDescription(teamNames);
                await interaction.reply({ embeds: [embed], ephemeral: true });
            }

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
