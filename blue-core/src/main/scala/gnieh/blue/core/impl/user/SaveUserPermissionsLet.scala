/*
 * This file is part of the \BlueLaTeX project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gnieh.blue
package core
package impl
package user

import http._
import couch.UserPermissions

import common._

import com.typesafe.config.Config

import tiscaf._

import gnieh.sohva.control.CouchClient

import scala.io.Source

import scala.util.Try

/** Save the user permissions with the given name.
 *
 *  @author Lucas Satabin
 */
class SaveUserPermissionsLet(username: String, val couch: CouchClient, config: Config, logger: Logger) extends SyncBlueLet(config, logger) with SyncAuthenticatedLet {

  def authenticatedAct(user: UserInfo)(implicit talk: HTalk): Try[Any] = {
    val namesOnly = talk.req.param("names_only").map(_ == "true").getOrElse(false)
    val manager = entityManager("blue_users")
    for(perms <- manager.getComponent[UserPermissions](f"org.couchdb.user:${user.name}"))
      yield perms match {
        case Some(p @ UserPermissions(_, perms)) =>
          if(namesOnly)
            talk.writeJson(perms.keys, p._rev.get)
          else
            talk.writeJson(perms, p._rev.get)
        case None =>
          talk.writeJson(Nil)
      }
  }
}
