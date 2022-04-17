package kelegram.server

import kelegram.server.oauth.githubOAuth
import kelegram.server.routes.*
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.http4k.core.Credentials
import org.http4k.core.Method.*
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.PolyHandler

val kek = """
            <pre>
              | __________________________________________________________________
              |  
              |  - - - - - - - -   K  E  L  E  G  R  A  M   - - - - - - - - - - -
              |  
              |  °*°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°**°       
              | o*  °°.   °°   .°.                                      **      
              | o° .**°  .**. .*o°            ...°.      .°.*o.         °o      
              | o°                           *.  oO.     .**oo.         °o      
              | o°                           .****°       ...         .°*o°..   
              | o°                                  .°°°°°°°°          ....°°*° 
              | o°                                  °°.                      .o*
              | o°                             °*.                    .°°°°°°°. 
              | o°                              °o.                   ..*o.     
              | o°                       °*..    °o.                    °o      
              | o°                        .°*°°..°*                     °o      
              | °*°.............          ...°°***....................°°*.      
              |   .°ooooooooooo*        .*ooooooooooo*°.................        
              |    .ooooooooooo*       °oooooooooooo°                           
              |    .ooooooooooo*     .*ooooooooooo°                             
              |    .ooooooooooo*   .*ooooooooooo*                               
              |    .ooooooooooo*  °ooooooooooo*.                                
              |    .oooooooooooo°ooooooooooo*.                                  
              |    .ooooooooooooooooooooooo°                                    
              |    .oooooooooooooooooooooo°                                     
              |    .ooooooooooooooooooooooo°                                    
              |    .oooooooooooooooooooooooo*                                   
              |    .oooooooooooooooooooooooooo.                                 
              |    .oooooooooooo*..oooooooooooo°                                
              |    .ooooooooooo*    *ooooooooooo°                               
              |    .ooooooooooo*     °ooooooooooo*                              
              |    .ooooooooooo*      .oooooooooooo.                            
              |    .ooooooooooo*       .*ooooooooooo°                           
              |    .ooooooooooo*         *ooooooooooo*                          
              |    .ooooooooooo*          °oooooooooooo.                        
              |    .ooooooooooo*           .oooooooooooo°                       
              |    .***********°             ************.                      
            </pre>
        """.trimMargin()

fun main() {
    val oauth = githubOAuth(
        Uri.of("https://github.com"),
        Credentials(Config.clientId, Config.clientSecret)
    )
    val root = "/" bind GET to {
        Response(OK).body(kek).header("Content-Type", "text/html; charset=utf-8")
    }

    println(kek)
    println(Config.allowedOrigins)

    val http = ServerFilters.Cors(
        CorsPolicy(OriginPolicy.AnyOf(Config.allowedOrigins),
            headers = listOf("Content-Type","Origin"),
            methods = listOf(GET,POST,PUT,OPTIONS),
            credentials = true
        )).then(
        routes(
            inviteRoutes(),
            roomRoutes(),
            ownedRoomRoutes(),
            accountRoutes(),
            oauth,
            root
        )
    )
    val ws = webSocket()

    PolyHandler(http, ws).asServer(Netty(Config.port)) .start()
}