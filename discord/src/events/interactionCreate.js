const { Events, ChatInputCommandInteraction } = require('discord.js');
const { logger } = require('../utils');

module.exports = {
    name: Events.InteractionCreate,

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {
            const commands = interaction.client['commands'];
            const command = commands.get(interaction.commandName);

            try { interaction.options.getSubcommand(); } catch (err) {
                logger.info(`${interaction.channel.id} ${interaction.user.tag}: /${interaction.commandName}`);
                await command.execute(interaction);
                return;
            }

            let commandName = `${interaction.commandName} ${interaction.options.getSubcommand()}`;
            logger.info(`${interaction.channel.id} ${interaction.user.tag}: /${commandName}`);
            await command.execute(interaction);

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
