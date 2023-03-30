const { Client, Collection, GatewayIntentBits } = require('discord.js');
const { logger } = require('./utils');

try {
    const client = new Client({
        intents: [
            GatewayIntentBits.Guilds,
            GatewayIntentBits.GuildMessages,
            GatewayIntentBits.MessageContent,
            GatewayIntentBits.GuildMembers
        ]
    });

    client.commands = new Collection();

    for (const command of require('./commands')) {
        client.commands.set(command.data.name, command);
    }

    for (const event of require('./events')) {
        client.on(event.name, (...args) => event.execute(...args));
    }

    process.on('unhandledRejection', async (reason) => {
        logger.error(reason.stack);
    });

    process.on('uncaughtException', async (err) => {
        logger.error(err.stack);
    });

    process.on('warning', async (warning) => {
        logger.warn(warning.stack);
    });

    client.login(process.env.DISCORD_TOKEN).then();

} catch (err) {
    logger.error(err.stack);
}
