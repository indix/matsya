package in.ashwanthkumar.matsya

import in.ashwanthkumar.slack.webhook.{Slack, SlackMessage}

trait Notifier {
  def info(message: String)
  def error(message: String)
}

class SlackNotifier(client: Option[Slack]) extends Notifier {
  override def info(message: String): Unit = client.foreach(_.push(new SlackMessage(message)))
  override def error(message: String): Unit = client.foreach(_.push(new SlackMessage(message)))
}
