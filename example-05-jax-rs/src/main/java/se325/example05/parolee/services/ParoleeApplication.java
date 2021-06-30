package se325.example05.parolee.services;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS application subclass for the Parolee Web service. This class is discovered by the JAX-RS run-time and is used
 * to obtain a reference to the ParoleeResource object that will process Web service requests.
 * 
 * The base URI for the Parolee Web service is:
 * 
 * http://<host-name>:<port>/[war_name/]paroleeServices.
 *
 */
@ApplicationPath("/paroleeServices")
public class ParoleeApplication extends Application {
   private Set<Object> singletons = new HashSet<>();

   public ParoleeApplication()
   {
      singletons.add(new ParoleeResource());
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }
}