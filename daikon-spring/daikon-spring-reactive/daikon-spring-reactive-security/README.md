# Daikon spring Reactive Security

This module contains a reactive WebClient which overrides the existing one from `spring-webflux`

## Authenticated Call

Authenticated calls are needed to call API like dataset, sharing, rating, ...
Those API are waiting a JWT token from a header `Authorization`.

In order to use reactive with non-blocking request, we need to pass through each request the token.
Otherwise, we lose the token between calls (example: `flatMap()`)

## Exchange Filter

This filter uses the reactor context to store the token and pass it through the next request using the `flatMap(next:exchange)`.

In order to use it add a `WebClient.Builder` bean like this one on your project:

```java
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient
                .builder()
                .filter(new JwtBearerExchangeFilterFunction());
    }
```

The context with the token need to be subscribed to the request.
So, this is an example to authenticate a call (`SharingWebClient`) :

```java
    public static <T> Mono<T> addSecurityContext(Mono<T> monoResponse) {
        return ReactiveSecurityContextHolder
        .getContext() //
        .map(SecurityContext::getAuthentication) //
        .flatMap(auth -> //
        monoResponse.subscriberContext(ReactiveSecurityContextHolder.withAuthentication(auth)));
        }

    addSecurityContext(sharingClient.getSharings(EntityType.DATASET)
        .flatMap(sharings -> sharingClient.getSharingSetEntity(EntityType.DATASET, sharings.getEntityId())));
```