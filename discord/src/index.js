const { Client, Collection, GatewayIntentBits } = require('discord.js');
const decode = require('jwt-decode');
const axios = require('axios');
const express = require('express');
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
    client.accounts = new Collection();

    for (const command of require('./commands')) {
        client.commands.set(command.data.name, command);
    }

    for (const event of require('./events')) {
        client.on(event.name, (...args) => event.execute(...args));
    }

    const app = express();
    const port = 13133;

    app.get('/bot/auth', async (req, res) => {
        const data = {
            code: req.query.code,
            client_id: process.env.CLIENT_ID,
            client_secret: process.env.CLIENT_SECRET,
            redirect_uri: `${process.env.URL}/bot/auth`,
            grant_type: 'authorization_code'
        };

        const id_token = await axios
            .post('https://www.googleapis.com/oauth2/v4/token', data)
            .then((res) => {
                return res.data.id_token;
            })
            .catch((err) => {
                logger.error(err.stack);
            });

        const backend = axios.create({
            headers: {
                Authorization: `Bearer ${id_token}`,
            }
        });

        const jwt_token = await backend
            .post(`http://login:13126/auth/token/generate`)
            .then((res) => {
                return res.data.access_token;
            })
            .catch((err) => {
                logger.error(err.stack);
            });

        for (const guild of client.guilds.cache.values()) {
            const members = await guild.members.fetch();
            const member = members.find(member => member.displayName === decode(jwt_token).laker_id);

            if (member) {
                res.send('Successfully logged in, you can now close this tab.')
                client.accounts.set(member.id, jwt_token);
                return;
            }
        }

        res.send('Account not found, please ensure your discord name is set to your laker net id.');
    });

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
    app.listen(port);

} catch (err) {
    logger.error(err.stack);
}
