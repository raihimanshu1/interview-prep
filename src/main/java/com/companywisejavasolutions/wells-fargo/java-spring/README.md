# Wells Fargo Java / Spring / Backend Questions

This section has one compile-safe Java study file per interview question. Each file is comment-first and human-centric: short answer, explanation, backend use case, a small code/comment snippet where useful, common mistakes, interview tips, and source links.

All answers in this section should follow the shared [ANSWER_FORMAT_STANDARD.md](ANSWER_FORMAT_STANDARD.md). Even simple questions should use a clear interview-ready shape: definition, simple terms, why it matters, example, safe vs unsafe cases, production mindset, quick mental model, and a speakable interview answer. LLD/HLD questions should use the deeper problem statement, requirements, approaches, data structures/classes, concurrency, distributed, production architecture, follow-ups, and senior-level answer format.

Generated interview-ready responses live in [interview-responses/README.md](interview-responses/README.md). This pack includes every Java question file in `questions/`, including files that are not yet listed in the table below.

See [SOURCES.md](SOURCES.md) for official follow-up references.

| # | Category | Question | File |
|---:|---|---|---|
| 1 | Core Java | Explain OOP principles with Java examples: encapsulation, inheritance, polymorphism, abstraction. | [OopPrinciples.java](questions/OopPrinciples.java) |
| 2 | Core Java | Difference between overloading and overriding. | [OverloadingVsOverriding.java](questions/OverloadingVsOverriding.java) |
| 3 | Core Java | Interface vs abstract class. When would you use each in Java 8+? | [InterfaceVsAbstractClass.java](questions/InterfaceVsAbstractClass.java) |
| 4 | Core Java | How do default and static methods in interfaces work? | [DefaultAndStaticInterfaceMethods.java](questions/DefaultAndStaticInterfaceMethods.java) |
| 5 | Core Java | How would you create a custom immutable Java class? | [CustomImmutableClass.java](questions/CustomImmutableClass.java) |
| 6 | Core Java | Why should immutable objects defensively copy mutable fields? | [DefensiveCopiesInImmutableObjects.java](questions/DefensiveCopiesInImmutableObjects.java) |
| 7 | Core Java | Difference between String, StringBuilder, and StringBuffer. | [StringVsBuilderVsBuffer.java](questions/StringVsBuilderVsBuffer.java) |
| 8 | Core Java | Explain equals() and hashCode() contract. | [EqualsAndHashCodeContract.java](questions/EqualsAndHashCodeContract.java) |
| 9 | Core Java | What happens if a mutable object is used as a HashMap key? | [MutableHashMapKey.java](questions/MutableHashMapKey.java) |
| 10 | Core Java | Difference between HashMap, LinkedHashMap, TreeMap, and ConcurrentHashMap. | [MapImplementations.java](questions/MapImplementations.java) |
| 11 | Core Java | How does HashMap work internally? | [HashMapInternals.java](questions/HashMapInternals.java) |
| 12 | Core Java | What changed in Java 8 HashMap collision handling? | [Java8HashMapCollisionHandling.java](questions/Java8HashMapCollisionHandling.java) |
| 13 | Core Java | Difference between fail-fast and fail-safe iterators. | [FailFastVsFailSafeIterators.java](questions/FailFastVsFailSafeIterators.java) |
| 14 | Core Java | Checked vs unchecked exceptions. What should service APIs expose? | [CheckedVsUncheckedExceptions.java](questions/CheckedVsUncheckedExceptions.java) |
| 15 | Core Java | Explain try-with-resources. | [TryWithResources.java](questions/TryWithResources.java) |
| 16 | Core Java | What is a memory leak in Java if garbage collection exists? | [JavaMemoryLeaks.java](questions/JavaMemoryLeaks.java) |
| 17 | Core Java | How do you read and analyze a heap dump? | [HeapDumpAnalysis.java](questions/HeapDumpAnalysis.java) |
| 18 | Core Java | Explain stack memory vs heap memory. | [StackVsHeapMemory.java](questions/StackVsHeapMemory.java) |
| 19 | Core Java | What are strong, weak, soft, and phantom references? | [ReferenceTypes.java](questions/ReferenceTypes.java) |
| 20 | Core Java | Explain class loading in Java. | [ClassLoading.java](questions/ClassLoading.java) |
| 21 | Java 8+ / Functional | Explain lambda expressions and functional interfaces. | [LambdasAndFunctionalInterfaces.java](questions/LambdasAndFunctionalInterfaces.java) |
| 22 | Java 8+ / Functional | Difference between map, flatMap, filter, reduce, and collect. | [StreamOperations.java](questions/StreamOperations.java) |
| 23 | Java 8+ / Functional | When should you avoid streams? | [WhenToAvoidStreams.java](questions/WhenToAvoidStreams.java) |
| 24 | Java 8+ / Functional | Sequential stream vs parallel stream. What can go wrong? | [SequentialVsParallelStreams.java](questions/SequentialVsParallelStreams.java) |
| 25 | Java 8+ / Functional | Explain Optional; when is it useful and when is it overused? | [OptionalUsage.java](questions/OptionalUsage.java) |
| 26 | Java 8+ / Functional | Method reference vs lambda. | [MethodReferenceVsLambda.java](questions/MethodReferenceVsLambda.java) |
| 27 | Java 8+ / Functional | How do default interface methods affect backward compatibility? | [DefaultMethodsCompatibility.java](questions/DefaultMethodsCompatibility.java) |
| 28 | Java 8+ / Functional | What are records and when would you use them? | [RecordsUsage.java](questions/RecordsUsage.java) |
| 29 | Java 8+ / Functional | What are sealed classes and where can they help domain modeling? | [SealedClasses.java](questions/SealedClasses.java) |
| 30 | Java 8+ / Functional | Explain date/time API changes after Java 8. | [JavaTimeApi.java](questions/JavaTimeApi.java) |
| 31 | Concurrency | Process vs thread. | [ProcessVsThread.java](questions/ProcessVsThread.java) |
| 32 | Concurrency | What does volatile guarantee and what does it not guarantee? | [VolatileGuarantees.java](questions/VolatileGuarantees.java) |
| 33 | Concurrency | synchronized vs ReentrantLock. | [SynchronizedVsReentrantLock.java](questions/SynchronizedVsReentrantLock.java) |
| 34 | Concurrency | Explain deadlock, livelock, and starvation. | [DeadlockLivelockStarvation.java](questions/DeadlockLivelockStarvation.java) |
| 35 | Concurrency | How do you debug a production deadlock? | [DebugProductionDeadlock.java](questions/DebugProductionDeadlock.java) |
| 36 | Concurrency | Difference between Runnable, Callable, Future, and CompletableFuture. | [RunnableCallableFutureCompletableFuture.java](questions/RunnableCallableFutureCompletableFuture.java) |
| 37 | Concurrency | How does an executor service work? | [ExecutorServiceBasics.java](questions/ExecutorServiceBasics.java) |
| 38 | Concurrency | Fixed thread pool vs cached thread pool. | [FixedVsCachedThreadPool.java](questions/FixedVsCachedThreadPool.java) |
| 39 | Concurrency | What is backpressure? | [Backpressure.java](questions/Backpressure.java) |
| 40 | Concurrency | Explain ConcurrentHashMap concurrency behavior. | [ConcurrentHashMapBehavior.java](questions/ConcurrentHashMapBehavior.java) |
| 41 | Concurrency | Atomic classes vs locks. | [AtomicClassesVsLocks.java](questions/AtomicClassesVsLocks.java) |
| 42 | Concurrency | How do you make a method thread-safe? | [ThreadSafeMethod.java](questions/ThreadSafeMethod.java) |
| 43 | Concurrency | How would you protect a banking transfer from race conditions? | [BankingTransferRaceConditions.java](questions/BankingTransferRaceConditions.java) |
| 44 | Concurrency | How do you design a retry without duplicating financial operations? | [RetryWithoutDuplicateFinancialOperations.java](questions/RetryWithoutDuplicateFinancialOperations.java) |
| 45 | Concurrency | Explain thread-local and its risks in app servers. | [ThreadLocalRisks.java](questions/ThreadLocalRisks.java) |
| 46 | Spring Boot / Microservices | Explain Spring IoC and dependency injection. | [SpringIocAndDependencyInjection.java](questions/SpringIocAndDependencyInjection.java) |
| 47 | Spring Boot / Microservices | Constructor injection vs field injection. | [ConstructorVsFieldInjection.java](questions/ConstructorVsFieldInjection.java) |
| 48 | Spring Boot / Microservices | Bean scope: singleton, prototype, request, session. | [BeanScopes.java](questions/BeanScopes.java) |
| 49 | Spring Boot / Microservices | How does Spring Boot auto-configuration work? | [SpringBootAutoConfiguration.java](questions/SpringBootAutoConfiguration.java) |
| 50 | Spring Boot / Microservices | Difference between @Component, @Service, @Repository, and @Controller. | [SpringStereotypes.java](questions/SpringStereotypes.java) |
| 51 | Spring Boot / Microservices | @RestController vs @Controller. | [RestControllerVsController.java](questions/RestControllerVsController.java) |
| 52 | Spring Boot / Microservices | How do you validate REST request payloads? | [ValidateRestPayloads.java](questions/ValidateRestPayloads.java) |
| 53 | Spring Boot / Microservices | How do you handle exceptions consistently in Spring Boot? | [SpringBootExceptionHandling.java](questions/SpringBootExceptionHandling.java) |
| 54 | Spring Boot / Microservices | How does @Transactional work? | [TransactionalBasics.java](questions/TransactionalBasics.java) |
| 55 | Spring Boot / Microservices | Common pitfalls of @Transactional self-invocation. | [TransactionalSelfInvocation.java](questions/TransactionalSelfInvocation.java) |
| 56 | Spring Boot / Microservices | Transaction propagation and isolation in Spring. | [TransactionPropagationIsolation.java](questions/TransactionPropagationIsolation.java) |
| 57 | Spring Boot / Microservices | How do you secure Spring Boot APIs? | [SecureSpringBootApis.java](questions/SecureSpringBootApis.java) |
| 58 | Spring Boot / Microservices | JWT vs opaque token vs session cookie. | [JwtOpaqueSession.java](questions/JwtOpaqueSession.java) |
| 59 | Spring Boot / Microservices | How would you implement OAuth2 resource server behavior? | [Oauth2ResourceServer.java](questions/Oauth2ResourceServer.java) |
| 60 | Spring Boot / Microservices | How do you version REST APIs? | [RestApiVersioning.java](questions/RestApiVersioning.java) |
| 61 | Spring Boot / Microservices | How do you design idempotent POST APIs? | [IdempotentPostApis.java](questions/IdempotentPostApis.java) |
| 62 | Spring Boot / Microservices | How do you test Spring Boot services: unit, slice, integration, contract? | [SpringBootTestingStrategy.java](questions/SpringBootTestingStrategy.java) |
| 63 | Spring Boot / Microservices | What belongs in a controller vs service vs repository? | [ControllerServiceRepository.java](questions/ControllerServiceRepository.java) |
| 64 | Spring Boot / Microservices | How do microservices communicate: REST, messaging, events? | [MicroserviceCommunication.java](questions/MicroserviceCommunication.java) |
| 65 | Spring Boot / Microservices | Saga vs two-phase commit. | [SagaVsTwoPhaseCommit.java](questions/SagaVsTwoPhaseCommit.java) |
| 66 | Spring Boot / Microservices | How do you prevent cascading failures? | [PreventCascadingFailures.java](questions/PreventCascadingFailures.java) |
| 67 | Spring Boot / Microservices | Circuit breaker, timeout, retry, and bulkhead: explain each. | [ResiliencePatterns.java](questions/ResiliencePatterns.java) |
| 68 | Spring Boot / Microservices | How do you make services observable? | [ServiceObservability.java](questions/ServiceObservability.java) |
| 69 | Spring Boot / Microservices | How do you handle schema evolution in event payloads? | [EventSchemaEvolution.java](questions/EventSchemaEvolution.java) |
| 70 | Spring Boot / Microservices | How do you migrate from a monolith to microservices safely? | [MonolithToMicroservicesMigration.java](questions/MonolithToMicroservicesMigration.java) |
