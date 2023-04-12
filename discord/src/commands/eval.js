const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('eval')
        .setDescription('Evaluate JavaScript code')
        .addStringOption(option =>
            option.setName('code')
                .setDescription('The code to evaluate')
                .setRequired(true)),

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {
            if (interaction.user.id !== '468854398806654976') {
                await interaction.reply('You do not have permission to use this command.');
                return;
            }

            await interaction.deferReply();

            let code = interaction.options.getString('code');
            if (code.includes('await')) code = `(async () => {${code}})();`;

            const output = await eval(code);
            if (output instanceof Promise) await output;

            let result = output;
            if (typeof output === 'function') result = output.toString();
            if (typeof output !== 'string') result = require('util').inspect(output, { depth: 0 });

            await interaction.editReply(`\`\`\`ansi\n${result}\n\`\`\``);

        } catch (err) {
            await interaction.editReply(`\`\`\`ansi\n${err.stack}\n\`\`\``);
            logger.error(err.stack);
        }
    }
};
