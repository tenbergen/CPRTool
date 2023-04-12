const { Events, REST, Routes, ActivityType } = require('discord.js');
const { logger } = require('../utils');

module.exports = {
    name: Events.ClientReady,

    /** @param {import('discord.js').Client} client */
    async execute(client) {
        try {
            const activities = [
                { type: ActivityType.Watching, name: `${client.guilds.cache.reduce((users, guild) => users + guild.memberCount, 0)} users` },
                { type: ActivityType.Watching, name: `${client.guilds.cache.size} servers` }
            ];

            client.user.setActivity(activities[0].name, { type: activities[0].type });
            let counter = 0;

            setInterval(async () => {
                try {
                    counter === activities.length - 1 ? counter = 0 : counter++;
                    client.user.setActivity(activities[counter].name, {type: activities[counter].type});

                } catch (err) {
                    logger.error(err.stack);
                }

            }, 30000);

            const commands = [];

            for (const command of require('../commands')) {
                commands.push(command.data.toJSON());
            }

            const rest = new REST({ version: '10' }).setToken(process.env.DISCORD_TOKEN);

            if (process.env.NODE_ENV === 'production') {
                await rest.put(Routes.applicationCommands(client.user.id), { body: commands });

            } else {
                await rest.put(Routes.applicationGuildCommands(client.user.id, process.env.TESTING_GUILD), { body: commands });
            }

            const userCount = client.guilds.cache.reduce((users, guild) => users + guild.memberCount, 0);
            logger.info(`Ready to serve ${userCount} users in ${client.guilds.cache.size} servers! Logged in as ${client.user.tag}`);

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
