package conversation

import akka.actor.typed.ActorSystem
import conversation.config.AppConfig
import conversation.models.{Conversation, ConversationTurn}
import conversation.services.{EC2Service, OllamaService}
import conversation.utils.ConversationLogger
import org.slf4j.LoggerFactory

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class ConversationalAgent(config: AppConfig)(implicit system: ActorSystem[_], ec: ExecutionContext) {
  private val logger = LoggerFactory.getLogger(getClass)
  private val ec2Service = new EC2Service(config.ec2Config)
  private val ollamaService = new OllamaService(config.ollamaConfig)
  private val conversationLogger = new ConversationLogger()

  def runConversation(initialPrompt: String): Future[String] = {
    val conversationId = UUID.randomUUID().toString

    def conversationLoop(
                          turns: List[ConversationTurn],
                          currentPrompt: String,
                          turnsRemaining: Int
                        ): Future[List[ConversationTurn]] = {
      if (turnsRemaining <= 0) {
        Future.successful(turns)
      } else {
        for {
          response <- ec2Service.query(currentPrompt)
          turn = ConversationTurn(currentPrompt, response)
          nextPrompt = ollamaService.generateNextPrompt(response)
          result <- conversationLoop(turns :+ turn, nextPrompt, turnsRemaining - 1)
        } yield result
      }
    }

    val conversationFuture = conversationLoop(List.empty, initialPrompt, config.maxTurns)

    conversationFuture.map { turns =>
      val conversation = Conversation(conversationId, turns)
      conversationLogger.saveConversation(conversation)
      conversationId
    }
  }
}