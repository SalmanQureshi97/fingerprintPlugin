var exec = require("cordova/exec");

exports.exportMethod = function (arg0, success, error) {
  exec(success, error, "FingerprintPlugin", "exportMethod", [arg0]);
};
