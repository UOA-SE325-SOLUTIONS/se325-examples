# SE325 Example 13 - Asynchronous Web Services
This project contains a parolee web service identical to [Example 12](../example-12-parolee-with-jpa), except:
- The structure has been changed to be much similar to that given in Assignment One,
- Test data is now initialized with a *.sql script (`db-init.sql`) instead of Java code, and
- Parolees may now have a Curfew - a period of time during which they must remain  in or near a particular location.
  - Users may subscribe to parolees to be notified if they venture outside their confinement location during a curfew period.

The new functionality is achieved using JAX-RS support for asynchronous web services, using the `@Suspended` annotation and `AsyncResponse` class. `ParoleeResource` has the web method `subscribeToParoleeViolations()` (line 435), which shows how we can create an async web method in this way. When a request is received at this endpoint, rather than directly returning a response, the response can be returned to the client at a later time using `AsyncResponse`'s `resume()` method, passing in an object to return as a response (or a `throwable` representing an error that occurred).

We can see where we eventually send the responses back to the subscribers, in the `SubscriptionManager` class. This class contains methods which process parole violations and, if found, notifies interested clients of those violations. The `processSubsFor()` method, which performs this processing, is called in `ParoleeResource`, lines 87 and 439.

Note on `SubscriptionManager` line 98, where we send a `Response` back to a client using the `resume()` method. Straight afterwards, we remove that `AsyncResposne` from our list of subs, because we can only ever send one response back. If a client wishes to continue receiving parole violation notifications, they must resubscribe by calling `POST /parolees/{id}/subscribe-to-violations` again.

In `ParoleeWebServiceIT`, we test the subscribe mechanism in the `testSubscribeToParoleViolation()` method (line 341). Here, we:
1. Send a subscription request
2. Verify that we don't immediately get a response (because there hasn't been a parole violation yet)
3. Add a movement record to a parolee, causing a parole violation
4. Verify that we now get our subscription response

Note the use of `client.target(url).request().async()` on line 346. This returns a `Future<Response>` rather than directly returning a `Response` object. The method makes the HTTP request on a background thread, so we can continue executing other code in the meantime. Then, when we're ready to look at the response (or indeed, to see if there is one), we can use the future's `get()` method, optionally supplying a timeout value.
