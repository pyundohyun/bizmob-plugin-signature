var exec = require('cordova/exec');

exports.openSignature = function(arg0, success, error) {
    exec(success, error, "Signature", "openSignature", [arg0]);
};