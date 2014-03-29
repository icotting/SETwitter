var Subscriber = function () {
  this.belongsTo('User');
  this.belongsTo('Feed');
};

exports.Subscriber = Subscriber;

