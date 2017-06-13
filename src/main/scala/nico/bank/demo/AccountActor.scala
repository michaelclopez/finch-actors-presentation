package nico.bank.demo

import akka.actor.{Actor, Props}
import nico.bank.demo.AccountActor._

import scala.util.Random

class AccountActor(accountId: String) extends Actor {

  val journal = Journal.For(accountId)

  override def receive: Receive = {
    case GetBalance           =>  sender() ! journal.account
    case AddBalance(balance)  =>  {
      journal.add(Transaction(transactionId, accountId, balance))

      sender() ! BalanceAdded
    }
    case RemoveBalance(balance) =>  {

      val b = journal.account.balance

      val st = journal.add(Transaction(transactionId, accountId, balance * -1))

      if (b == st.balance) {
        sender() ! InsufficientFoundsFor(balance)
      } else {
        sender() ! BalanceRemoved
      }
    }
  }

  private def transactionId = Random.nextLong().toString
}

object AccountActor {

  def props(accountId: String): Props = Props(new AccountActor(accountId))

  case object GetBalance
  case object BalanceAdded
  case class AddBalance(balance: Int)

  case class RemoveBalance(balance: Int)
  case class InsufficientFoundsFor(balance: Int)

  case object BalanceRemoved
}