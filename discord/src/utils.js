const { createLogger, format, transports } = require('winston');
const { combine, timestamp, colorize, printf } = format;

try {
    const myFormat = printf(({level, message, timestamp}) => {
        return `${timestamp} [ ${level} ] ${message}`;
    });

    const logger = createLogger({
        level: 'debug',
        format: combine(
            timestamp({format: 'YYYY/MM/DD HH:mm:ss'}),
            colorize({colors: {info: 'cyan', error: 'red', warn: 'yellow', debug: 'gray'}}),
            myFormat
        ),
        transports: [
            new transports.Console()
        ]
    });

    module.exports = { logger };

} catch (err) {
    console.error(err.stack);
}
