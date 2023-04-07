const { SlashCommandBuilder, ChatInputCommandInteraction, EmbedBuilder, AttachmentBuilder } = require('discord.js');
const { logger, authenticate } = require('../utils');
const axios = require('axios');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('list')
        .setDescription('Lists all your scores, assignments, solutions, students, or teams')
        .addSubcommand(subcommand => subcommand
            .setName('assignments')
            .setDescription('Lists all the assignments for the class'))
        .addSubcommand(subcommand => subcommand
            .setName('scores')
            .setDescription('Lists all your scores for the class'))
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
            const subcommand = interaction.options.getSubcommand();
            const { headers, roles, courseId } = await authenticate(interaction)
                .catch(async (err) => {
                    await interaction.reply({ content: err.message, ephemeral: true });
                    logger.error('User not logged in');
                });

            if (subcommand === 'assignments') {
                const url = 'http://professor-assignment:13130/assignments/professor';
                const assignments = await axios
                    .get(`${url}/courses/${courseId}/assignments`, headers)
                    .then((res) => {
                        return res.data;
                    })
                    .catch(async (err) => {
                        await interaction.reply({ content: 'Error getting assignments.', ephemeral: true });
                        logger.error(err.stack);
                    });

                let counter = 1;
                const embed = new EmbedBuilder()
                    .setTitle('Assignments for ' + interaction.member.displayName);

                for (const assignment of assignments) {
                    embed.addFields({
                        name: `${counter}. ${assignment.assignment_name}`,
                        value: `Due: ${assignment.due_date}\nScore: ${assignment.grade}`
                    });

                    counter++;
                }

                await interaction.reply({ embeds: [embed] });

            } else if (subcommand === 'scores') {
                if (!roles.includes('student')) {
                    await interaction.reply({ content: 'You are not a student.', ephemeral: true });
                    return;
                }

                const url = `http://student-assignment:13131/assignments/student`;
                const submissions = await axios
                    .get(`${url}/${courseId}/${interaction.member.displayName}/submissions`, headers)
                    .then((res) => {
                        return res.data;
                    })
                    .catch(async (err) => {
                        await interaction.reply({ content: 'Error getting scores.', ephemeral: true });
                        logger.error(err.stack);
                    });

                let counter = 1;
                const embed = new EmbedBuilder()
                    .setTitle('Scores for ' + interaction.member.displayName);

                for (const submission of submissions) {
                    embed.addFields({
                        name: `${counter}. ${submission.assignment_name}`,
                        value: `Score: ${submission.grade}`
                    });

                    counter++;
                }

                await interaction.reply({ embeds: [embed], ephemeral: true });

            } else if (subcommand === 'solutions') {
                const studentId = interaction.member.displayName;
                const url = 'http://student-assignment:13131/assignments/student';
                const submissions = axios
                    .get(`${url}/${courseId}/${studentId}/submissions`, headers)
                    .then((res) => {
                        return res.data;
                    })
                    .catch(async (err) => {
                        await interaction.reply({ content: 'Error getting solutions.', ephemeral: true });
                        logger.error(err.stack);
                    });

                let counter = 1;
                const files = [];
                const embed = new EmbedBuilder()
                    .setTitle('Solutions for ' + interaction.member.displayName);

                for (const submission of submissions) {
                    files.push(new AttachmentBuilder(Buffer.from(submission.submission_data, 'base64'), { name: `${submission.assignment_name}.pdf` }));
                    embed.addFields({
                        name: `${counter}. ${submission.assignment_name}`,
                        value: `Score: ${submission.grade}`
                    });

                    counter++;
                }

                await interaction.reply({ embeds: [embed], files: files, ephemeral: true });

            } else if (subcommand === 'students') {
                if (!roles.includes('professor')) {
                    await interaction.reply({ content: 'You are not a professor.', ephemeral: true });
                    return;
                }

                const url = 'http://course-viewer:13128/view';
                const students = await axios.get(`${url}/courses/${courseId}/students`, headers)
                    .then((res) => {
                        return res.data;
                    })
                    .catch(async (err) => {
                        await interaction.reply({ content: 'Error getting students.', ephemeral: true });
                        logger.error(err.stack);
                    });

                const embed = new EmbedBuilder()
                    .setTitle('Students');

                for (const student of students) {
                    embed.addFields({
                        name: `${student.first_name} ${student.last_name}`,
                        value: `Student ID: ${student.student_id}`
                    });
                }

                await interaction.reply({ embeds: [embed], ephemeral: true });

            } else if (subcommand === 'teams') {
                if (!roles.includes('professor')) {
                    await interaction.reply({ content: 'You are not a professor.', ephemeral: true });
                    return;
                }

                const url = 'http://peer-review-teams:13129/teams/team';
                const teams = await axios
                    .get(`${url}/get/all/${courseId}`, headers)
                    .then((res) => {
                        return res.data;
                    })
                    .catch(async (err) => {
                        await interaction.reply({ content: 'Error getting teams.', ephemeral: true });
                        logger.error(err.stack);
                    });

                const embed = new EmbedBuilder()
                    .setTitle('Teams');

                for (const team of teams) {
                    embed.addFields({
                        name: `${team.team_id}`,
                        value: `Full: ${team.team_full}`
                    });
                }

                await interaction.reply({ embeds: [embed], ephemeral: true });
            }

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
