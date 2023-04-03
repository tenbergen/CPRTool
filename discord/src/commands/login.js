const { SlashCommandBuilder, ChatInputCommandInteraction } = require('discord.js');
const express = require('express');
const { logger } = require('../utils');
const axios = require("axios");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('login')
        .setDescription('Login with your Google account'),

    /** @param {ChatInputCommandInteraction} interaction */
    async execute(interaction) {
        try {
            const url = 'https://accounts.google.com/o/oauth2/v2/auth' +
                '?access_type=offline' +
                `&client_id=${process.env.CLIENT_ID}` +
                `&redirect_uri=${process.env.URL}/bot/auth` +
                '&response_type=code' +
                '&scope=email%20openid%20profile';

            await interaction.reply(`[Click here](${url}) to login with your Google account.`);

            const app = express();
            const port = 13133;

            app.get('/bot/auth', async (req, res) => {
                res.send('You can close this window now.');

                const data = {
                    code: req.query.code,
                    client_id: process.env.CLIENT_ID,
                    client_secret: process.env.CLIENT_SECRET,
                    redirect_uri: `${process.env.URL}/bot/auth`,
                    grant_type: 'authorization_code'
                };

                const token = await axios
                    .post('https://www.googleapis.com/oauth2/v4/token', data)
                    .then((res) => {
                        return res.data.id_token;
                    })
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                const config = {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    }
                }

                const jwt_token = await axios
                    .post(`http://login:13126/auth/token/generate`, null, config)
                    .then((res) => {
                        return res.data.access_token;
                    })
                    .catch((err) => {
                        logger.error(err.stack);
                    });

                interaction.client.accounts.set(interaction.user.id, jwt_token);
            });

            app.listen(port, () => {
                logger.info(`Listening on port ${port}`);
            });

        } catch (err) {
            logger.error(err.stack);
        }
    }
};
