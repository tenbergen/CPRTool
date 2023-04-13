const decode = require('jwt-decode');
const { createLogger, format, transports } = require('winston');
const axios = require('axios');
const { combine, timestamp, colorize, printf } = format;

module.exports = {
    logger: createLogger({
        level: 'debug',
        format: combine(
            timestamp({ format: 'YYYY/MM/DD HH:mm:ss' }),
            colorize({ colors: { info: 'cyan', error: 'red', warn: 'yellow', debug: 'gray' }}),
            printf(({ level, message, timestamp }) => {
                return `${timestamp} [ ${level} ] ${message}`;
            })
        ),
        transports: [
            new transports.Console()
        ]
    }),

    authenticate: async (interaction) => {
        const auth = { headers: {}, roles: [], courseId: '' };
        const token = interaction.client.accounts.get(interaction.user.id);

        if (!token) {
            const url = 'https://accounts.google.com/o/oauth2/v2/auth' +
                '?access_type=offline' +
                `&client_id=${process.env.CLIENT_ID}` +
                `&redirect_uri=${process.env.URL}/bot/auth` +
                '&response_type=code' +
                '&scope=email%20openid%20profile';

            throw new Error(`You are not logged in! Please [click here](${url}) to log in.`);
        }

        auth.headers = {
            headers: {
                Authorization: `Bearer ${token}`
            }
        };

        auth.roles = decode(token).groups;
        auth.courseId = interaction.guild.name;

        // auth.courseId = await axios
        //     .get('http://course-viewer:13128/view/professor/courses', auth.headers)
        //     .then((res) => {
        //         return res.data.find(course => course.course_name === interaction.guild.name).course_id;
        //     })
        //     .catch((err) => {
        //         console.error(err.stack);
        //     });

        return auth;
    },
};
