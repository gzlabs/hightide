(function() {
    Prism.languages.conf = {
        'comment': /#.*\n/g,
        'string': /("|')(\\?.)*?\1/g,
        'keyword': /\b(dev|test|prod|debug|info|trace|warn)\b/g,
        'boolean': /\b(true|false)\b/g,
        'number': /\b-?(0x[\dA-Fa-f]+|\d*\.?\d+([Ee]-?\d+)?)\b/g,
        'operator': /[-+]{1,2}|!|&lt;=?|>=?|={1,3}|:|(&amp;){1,2}|\|?\||\?|\*|\/|\~|\^|\%/g,
        'punctuation': /[{}[\];(),.]/g
    }
})();