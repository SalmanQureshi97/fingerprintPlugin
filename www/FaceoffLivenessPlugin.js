var exec = require("cordova/exec");

exports.coolMethod = function (arg0, success, error) {
  exec(success, error, "FaceoffLivenessPlugin", "coolMethod", [arg0]);
};

exports.unCoolMethod = function (arg0, success, error) {
  exec(success, error, "FaceoffLivenessPlugin", "unCoolMethod", [arg0]);
};
