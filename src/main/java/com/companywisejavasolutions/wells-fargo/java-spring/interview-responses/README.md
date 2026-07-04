# Wells Fargo Java / Spring Interview Responses

Generated response pack for every Java question file in `questions/`. These responses follow `ANSWER_FORMAT_STANDARD.md` and include mandatory extra details: forward compatibility, backward compatibility, semantic versioning, big-company API evolution mindset, interview follow-ups, and related design patterns.

Total responses: 120

| # | Category | Question | Response | Source |
|---:|---|---|---|---|
| 1 | design | How do you design API rate limiting? | [ApiRateLimiting.md](ApiRateLimiting.md) | [ApiRateLimiting.java](../questions/ApiRateLimiting.java) |
| 2 | concurrency | Atomic classes vs locks. | [AtomicClassesVsLocks.md](AtomicClassesVsLocks.md) | [AtomicClassesVsLocks.java](../questions/AtomicClassesVsLocks.java) |
| 3 | concurrency | What is backpressure? | [Backpressure.md](Backpressure.md) | [Backpressure.java](../questions/Backpressure.java) |
| 4 | concept | How would you protect a banking transfer from race conditions? | [BankingTransferRaceConditions.md](BankingTransferRaceConditions.md) | [BankingTransferRaceConditions.java](../questions/BankingTransferRaceConditions.java) |
| 5 | spring | Bean scope: singleton, prototype, request, session. | [BeanScopes.md](BeanScopes.md) | [BeanScopes.java](../questions/BeanScopes.java) |
| 6 | concurrency | Blocking I/O vs non-blocking I/O in Java. | [BlockingVsNonBlockingIO.md](BlockingVsNonBlockingIO.md) | [BlockingVsNonBlockingIO.java](../questions/BlockingVsNonBlockingIO.java) |
| 7 | concept | Blue-green vs canary deployments. | [BlueGreenCanaryDeployments.md](BlueGreenCanaryDeployments.md) | [BlueGreenCanaryDeployments.java](../questions/BlueGreenCanaryDeployments.java) |
| 8 | concept | Explain PECS: producer extends, consumer super. | [BoundedWildcardsPECS.md](BoundedWildcardsPECS.md) | [BoundedWildcardsPECS.java](../questions/BoundedWildcardsPECS.java) |
| 9 | api | What is CQRS and when is it useful? | [CQRSInJavaMicroservices.md](CQRSInJavaMicroservices.md) | [CQRSInJavaMicroservices.java](../questions/CQRSInJavaMicroservices.java) |
| 10 | api | Cache-aside vs write-through vs write-behind. | [CacheAsideVsWriteThrough.md](CacheAsideVsWriteThrough.md) | [CacheAsideVsWriteThrough.java](../questions/CacheAsideVsWriteThrough.java) |
| 11 | api | Checked vs unchecked exceptions. What should service APIs expose? | [CheckedVsUncheckedExceptions.md](CheckedVsUncheckedExceptions.md) | [CheckedVsUncheckedExceptions.java](../questions/CheckedVsUncheckedExceptions.java) |
| 12 | concept | Explain class loading in Java. | [ClassLoading.md](ClassLoading.md) | [ClassLoading.java](../questions/ClassLoading.java) |
| 13 | concurrency | How do you compose CompletableFuture workflows safely? | [CompletableFutureCompositionAdvanced.md](CompletableFutureCompositionAdvanced.md) | [CompletableFutureCompositionAdvanced.java](../questions/CompletableFutureCompositionAdvanced.java) |
| 14 | concurrency | Explain ConcurrentHashMap concurrency behavior. | [ConcurrentHashMapBehavior.md](ConcurrentHashMapBehavior.md) | [ConcurrentHashMapBehavior.java](../questions/ConcurrentHashMapBehavior.java) |
| 15 | concept | Constructor injection vs field injection. | [ConstructorVsFieldInjection.md](ConstructorVsFieldInjection.md) | [ConstructorVsFieldInjection.java](../questions/ConstructorVsFieldInjection.java) |
| 16 | api | What is contract testing and why does it matter? | [ContractTestingPact.md](ContractTestingPact.md) | [ContractTestingPact.java](../questions/ContractTestingPact.java) |
| 17 | spring | What belongs in a controller vs service vs repository? | [ControllerServiceRepository.md](ControllerServiceRepository.md) | [ControllerServiceRepository.java](../questions/ControllerServiceRepository.java) |
| 18 | concept | How would you create a custom immutable Java class? | [CustomImmutableClass.md](CustomImmutableClass.md) | [CustomImmutableClass.java](../questions/CustomImmutableClass.java) |
| 19 | spring | What should a Java backend engineer know about database indexing? | [DatabaseIndexingForJavaServices.md](DatabaseIndexingForJavaServices.md) | [DatabaseIndexingForJavaServices.java](../questions/DatabaseIndexingForJavaServices.java) |
| 20 | concurrency | Explain deadlock, livelock, and starvation. | [DeadlockLivelockStarvation.md](DeadlockLivelockStarvation.md) | [DeadlockLivelockStarvation.java](../questions/DeadlockLivelockStarvation.java) |
| 21 | concurrency | How do you debug a production deadlock? | [DebugProductionDeadlock.md](DebugProductionDeadlock.md) | [DebugProductionDeadlock.java](../questions/DebugProductionDeadlock.java) |
| 22 | concept | How do default and static methods in interfaces work? | [DefaultAndStaticInterfaceMethods.md](DefaultAndStaticInterfaceMethods.md) | [DefaultAndStaticInterfaceMethods.java](../questions/DefaultAndStaticInterfaceMethods.java) |
| 23 | api | How do default interface methods affect backward compatibility? | [DefaultMethodsCompatibility.md](DefaultMethodsCompatibility.md) | [DefaultMethodsCompatibility.java](../questions/DefaultMethodsCompatibility.java) |
| 24 | concept | Why should immutable objects defensively copy mutable fields? | [DefensiveCopiesInImmutableObjects.md](DefensiveCopiesInImmutableObjects.md) | [DefensiveCopiesInImmutableObjects.java](../questions/DefensiveCopiesInImmutableObjects.java) |
| 25 | design | Which design patterns matter most for senior Java backend interviews? | [DesignPatternsSeniorJava.md](DesignPatternsSeniorJava.md) | [DesignPatternsSeniorJava.java](../questions/DesignPatternsSeniorJava.java) |
| 26 | concept | Explain distributed tracing with OpenTelemetry. | [DistributedTracingOpenTelemetry.md](DistributedTracingOpenTelemetry.md) | [DistributedTracingOpenTelemetry.java](../questions/DistributedTracingOpenTelemetry.java) |
| 27 | concept | How do you optimize Docker images for Java applications? | [DockerImageOptimizationJava.md](DockerImageOptimizationJava.md) | [DockerImageOptimizationJava.java](../questions/DockerImageOptimizationJava.java) |
| 28 | api | Explain equals() and hashCode() contract. | [EqualsAndHashCodeContract.md](EqualsAndHashCodeContract.md) | [EqualsAndHashCodeContract.java](../questions/EqualsAndHashCodeContract.java) |
| 29 | concurrency | What is escape analysis and why should senior Java developers care? | [EscapeAnalysis.md](EscapeAnalysis.md) | [EscapeAnalysis.java](../questions/EscapeAnalysis.java) |
| 30 | api | How do you handle schema evolution in event payloads? | [EventSchemaEvolution.md](EventSchemaEvolution.md) | [EventSchemaEvolution.java](../questions/EventSchemaEvolution.java) |
| 31 | concurrency | How does an executor service work? | [ExecutorServiceBasics.md](ExecutorServiceBasics.md) | [ExecutorServiceBasics.java](../questions/ExecutorServiceBasics.java) |
| 32 | concept | Difference between fail-fast and fail-safe iterators. | [FailFastVsFailSafeIterators.md](FailFastVsFailSafeIterators.md) | [FailFastVsFailSafeIterators.java](../questions/FailFastVsFailSafeIterators.java) |
| 33 | spring | How do feature flags help production Java releases? | [FeatureFlagsJavaServices.md](FeatureFlagsJavaServices.md) | [FeatureFlagsJavaServices.java](../questions/FeatureFlagsJavaServices.java) |
| 34 | api | Fixed thread pool vs cached thread pool. | [FixedVsCachedThreadPool.md](FixedVsCachedThreadPool.md) | [FixedVsCachedThreadPool.java](../questions/FixedVsCachedThreadPool.java) |
| 35 | concurrency | Explain ForkJoinPool and work stealing. | [ForkJoinWorkStealing.md](ForkJoinWorkStealing.md) | [ForkJoinWorkStealing.java](../questions/ForkJoinWorkStealing.java) |
| 36 | concept | How do you analyze GC logs and tune memory? | [GCLogTuning.md](GCLogTuning.md) | [GCLogTuning.java](../questions/GCLogTuning.java) |
| 37 | concurrency | Compare G1 GC and ZGC for senior Java backend systems. | [GarbageCollectorsG1ZGC.md](GarbageCollectorsG1ZGC.md) | [GarbageCollectorsG1ZGC.java](../questions/GarbageCollectorsG1ZGC.java) |
| 38 | concept | Explain Java generics type erasure and its production consequences. | [GenericsTypeErasureDeepDive.md](GenericsTypeErasureDeepDive.md) | [GenericsTypeErasureDeepDive.java](../questions/GenericsTypeErasureDeepDive.java) |
| 39 | design | High-level design a scalable payment processing system. | [HLDScalablePaymentSystem.md](HLDScalablePaymentSystem.md) | [HLDScalablePaymentSystem.java](../questions/HLDScalablePaymentSystem.java) |
| 40 | api | How does HashMap work internally? | [HashMapInternals.md](HashMapInternals.md) | [HashMapInternals.java](../questions/HashMapInternals.java) |
| 41 | concept | How do you read and analyze a heap dump? | [HeapDumpAnalysis.md](HeapDumpAnalysis.md) | [HeapDumpAnalysis.java](../questions/HeapDumpAnalysis.java) |
| 42 | api | How do you design idempotent POST APIs? | [IdempotentPostApis.md](IdempotentPostApis.md) | [IdempotentPostApis.java](../questions/IdempotentPostApis.java) |
| 43 | concept | Interface vs abstract class. When would you use each in Java 8+? | [InterfaceVsAbstractClass.md](InterfaceVsAbstractClass.md) | [InterfaceVsAbstractClass.java](../questions/InterfaceVsAbstractClass.java) |
| 44 | concept | What changed in Java 8 HashMap collision handling? | [Java8HashMapCollisionHandling.md](Java8HashMapCollisionHandling.md) | [Java8HashMapCollisionHandling.java](../questions/Java8HashMapCollisionHandling.java) |
| 45 | concept | What is a memory leak in Java if garbage collection exists? | [JavaMemoryLeaks.md](JavaMemoryLeaks.md) | [JavaMemoryLeaks.java](../questions/JavaMemoryLeaks.java) |
| 46 | concept | What is JPMS and when does it matter? | [JavaModulesJPMS.md](JavaModulesJPMS.md) | [JavaModulesJPMS.java](../questions/JavaModulesJPMS.java) |
| 47 | api | Explain date/time API changes after Java 8. | [JavaTimeApi.md](JavaTimeApi.md) | [JavaTimeApi.java](../questions/JavaTimeApi.java) |
| 48 | concept | Explain JIT compilation and warm-up in Java. | [JitCompilation.md](JitCompilation.md) | [JitCompilation.java](../questions/JitCompilation.java) |
| 49 | spring | Explain JPA entity lifecycle states. | [JPAEntityLifecycle.md](JPAEntityLifecycle.md) | [JPAEntityLifecycle.java](../questions/JPAEntityLifecycle.java) |
| 50 | api | JWT vs opaque token vs session cookie. | [JWTOpaqueSession.md](JWTOpaqueSession.md) | [JWTOpaqueSession.java](../questions/JWTOpaqueSession.java) |
| 51 | api | How do Kafka consumer groups work? | [KafkaConsumerGroups.md](KafkaConsumerGroups.md) | [KafkaConsumerGroups.java](../questions/KafkaConsumerGroups.java) |
| 52 | api | What does exactly-once mean in Kafka and what does it not mean? | [KafkaExactlyOnce.md](KafkaExactlyOnce.md) | [KafkaExactlyOnce.java](../questions/KafkaExactlyOnce.java) |
| 53 | api | How do partitioning and ordering work in Kafka? | [KafkaPartitioningOrdering.md](KafkaPartitioningOrdering.md) | [KafkaPartitioningOrdering.java](../questions/KafkaPartitioningOrdering.java) |
| 54 | spring | Readiness vs liveness probes for Spring Boot services. | [KubernetesReadinessLiveness.md](KubernetesReadinessLiveness.md) | [KubernetesReadinessLiveness.java](../questions/KubernetesReadinessLiveness.java) |
| 55 | design | Low-level design a payment transfer service. | [LLDPaymentService.md](LLDPaymentService.md) | [LLDPaymentService.java](../questions/LLDPaymentService.java) |
| 56 | concept | Explain lambda expressions and functional interfaces. | [LambdasAndFunctionalInterfaces.md](LambdasAndFunctionalInterfaces.md) | [LambdasAndFunctionalInterfaces.java](../questions/LambdasAndFunctionalInterfaces.java) |
| 57 | spring | What are common lazy loading pitfalls in Spring Boot? | [LazyLoadingPitfalls.md](LazyLoadingPitfalls.md) | [LazyLoadingPitfalls.java](../questions/LazyLoadingPitfalls.java) |
| 58 | concurrency | What do senior Java machine-coding rounds evaluate? | [MachineCodingJavaBackend.md](MachineCodingJavaBackend.md) | [MachineCodingJavaBackend.java](../questions/MachineCodingJavaBackend.java) |
| 59 | api | Difference between HashMap, LinkedHashMap, TreeMap, and ConcurrentHashMap. | [MapImplementations.md](MapImplementations.md) | [MapImplementations.java](../questions/MapImplementations.java) |
| 60 | concept | What is Metaspace and how do classloader leaks happen? | [MetaspaceAndClassMetadata.md](MetaspaceAndClassMetadata.md) | [MetaspaceAndClassMetadata.java](../questions/MetaspaceAndClassMetadata.java) |
| 61 | concept | Method reference vs lambda. | [MethodReferenceVsLambda.md](MethodReferenceVsLambda.md) | [MethodReferenceVsLambda.java](../questions/MethodReferenceVsLambda.java) |
| 62 | api | How do microservices communicate: REST, messaging, events? | [MicroserviceCommunication.md](MicroserviceCommunication.md) | [MicroserviceCommunication.java](../questions/MicroserviceCommunication.java) |
| 63 | design | How do you migrate from a monolith to microservices safely? | [MonolithToMicroservicesMigration.md](MonolithToMicroservicesMigration.md) | [MonolithToMicroservicesMigration.java](../questions/MonolithToMicroservicesMigration.java) |
| 64 | concept | What happens if a mutable object is used as a HashMap key? | [MutableHashMapKey.md](MutableHashMapKey.md) | [MutableHashMapKey.java](../questions/MutableHashMapKey.java) |
| 65 | spring | What is the N+1 query problem in Hibernate/JPA? | [NPlusOneHibernate.md](NPlusOneHibernate.md) | [NPlusOneHibernate.java](../questions/NPlusOneHibernate.java) |
| 66 | concurrency | Explain Java NIO Selector at a high level. | [NIOSelector.md](NIOSelector.md) | [NIOSelector.java](../questions/NIOSelector.java) |
| 67 | api | How would you implement OAuth2 resource server behavior? | [Oauth2ResourceServer.md](Oauth2ResourceServer.md) | [Oauth2ResourceServer.java](../questions/Oauth2ResourceServer.java) |
| 68 | concept | Explain OOP principles with Java examples: encapsulation, inheritance, polymorphism, abstraction. | [OopPrinciples.md](OopPrinciples.md) | [OopPrinciples.java](../questions/OopPrinciples.java) |
| 69 | concurrency | Optimistic vs pessimistic locking in JPA/database systems. | [OptimisticVsPessimisticLocking.md](OptimisticVsPessimisticLocking.md) | [OptimisticVsPessimisticLocking.java](../questions/OptimisticVsPessimisticLocking.java) |
| 70 | concept | Explain Optional; when is it useful and when is it overused? | [OptionalUsage.md](OptionalUsage.md) | [OptionalUsage.java](../questions/OptionalUsage.java) |
| 71 | concept | Difference between overloading and overriding. | [OverloadingVsOverriding.md](OverloadingVsOverriding.md) | [OverloadingVsOverriding.java](../questions/OverloadingVsOverriding.java) |
| 72 | concept | Pattern matching for switch in Java 21. | [PatternMatchingSwitch.md](PatternMatchingSwitch.md) | [PatternMatchingSwitch.java](../questions/PatternMatchingSwitch.java) |
| 73 | api | How do you prevent cascading failures? | [PreventCascadingFailures.md](PreventCascadingFailures.md) | [PreventCascadingFailures.java](../questions/PreventCascadingFailures.java) |
| 74 | concurrency | Process vs thread. | [ProcessVsThread.md](ProcessVsThread.md) | [ProcessVsThread.java](../questions/ProcessVsThread.java) |
| 75 | concurrency | Reactive programming vs virtual threads in Spring applications. | [ReactiveVsVirtualThreads.md](ReactiveVsVirtualThreads.md) | [ReactiveVsVirtualThreads.java](../questions/ReactiveVsVirtualThreads.java) |
| 76 | concept | What are record patterns and where are they useful? | [RecordPatterns.md](RecordPatterns.md) | [RecordPatterns.java](../questions/RecordPatterns.java) |
| 77 | concept | What are records and when would you use them? | [RecordsUsage.md](RecordsUsage.md) | [RecordsUsage.java](../questions/RecordsUsage.java) |
| 78 | api | Explain cache-aside with Redis in Java services. | [RedisCacheAsidePattern.md](RedisCacheAsidePattern.md) | [RedisCacheAsidePattern.java](../questions/RedisCacheAsidePattern.java) |
| 79 | concept | What are strong, weak, soft, and phantom references? | [ReferenceTypes.md](ReferenceTypes.md) | [ReferenceTypes.java](../questions/ReferenceTypes.java) |
| 80 | concept | Circuit breaker, timeout, retry, and bulkhead: explain each. | [ResiliencePatterns.md](ResiliencePatterns.md) | [ResiliencePatterns.java](../questions/ResiliencePatterns.java) |
| 81 | api | How do you version REST APIs? | [RestApiVersioning.md](RestApiVersioning.md) | [RestApiVersioning.java](../questions/RestApiVersioning.java) |
| 82 | api | @RestController vs @Controller. | [RestControllerVsController.md](RestControllerVsController.md) | [RestControllerVsController.java](../questions/RestControllerVsController.java) |
| 83 | concept | How do you design a retry without duplicating financial operations? | [RetryWithoutDuplicateFinancialOperations.md](RetryWithoutDuplicateFinancialOperations.md) | [RetryWithoutDuplicateFinancialOperations.java](../questions/RetryWithoutDuplicateFinancialOperations.java) |
| 84 | concurrency | Difference between Runnable, Callable, Future, and CompletableFuture. | [RunnableCallableFutureCompletableFuture.md](RunnableCallableFutureCompletableFuture.md) | [RunnableCallableFutureCompletableFuture.java](../questions/RunnableCallableFutureCompletableFuture.java) |
| 85 | spring | Explain SOLID principles in Spring service design. | [SOLIDInSpringServices.md](SOLIDInSpringServices.md) | [SOLIDInSpringServices.java](../questions/SOLIDInSpringServices.java) |
| 86 | api | Saga vs two-phase commit. | [SagaVsTwoPhaseCommit.md](SagaVsTwoPhaseCommit.md) | [SagaVsTwoPhaseCommit.java](../questions/SagaVsTwoPhaseCommit.java) |
| 87 | concurrency | Scoped values vs ThreadLocal in modern Java. | [ScopedValuesVsThreadLocal.md](ScopedValuesVsThreadLocal.md) | [ScopedValuesVsThreadLocal.java](../questions/ScopedValuesVsThreadLocal.java) |
| 88 | concept | What are sealed classes and where can they help domain modeling? | [SealedClasses.md](SealedClasses.md) | [SealedClasses.java](../questions/SealedClasses.java) |
| 89 | api | How do you secure Spring Boot APIs? | [SecureSpringBootApis.md](SecureSpringBootApis.md) | [SecureSpringBootApis.java](../questions/SecureSpringBootApis.java) |
| 90 | concept | What are sequenced collections in Java 21? | [SequencedCollections.md](SequencedCollections.md) | [SequencedCollections.java](../questions/SequencedCollections.java) |
| 91 | concurrency | Sequential stream vs parallel stream. What can go wrong? | [SequentialVsParallelStreams.md](SequentialVsParallelStreams.md) | [SequentialVsParallelStreams.java](../questions/SequentialVsParallelStreams.java) |
| 92 | design | Explain service discovery in microservices. | [ServiceDiscovery.md](ServiceDiscovery.md) | [ServiceDiscovery.java](../questions/ServiceDiscovery.java) |
| 93 | spring | How do you make services observable? | [ServiceObservability.md](ServiceObservability.md) | [ServiceObservability.java](../questions/ServiceObservability.java) |
| 94 | spring | How does Spring Boot auto-configuration work? | [SpringBootAutoConfiguration.md](SpringBootAutoConfiguration.md) | [SpringBootAutoConfiguration.java](../questions/SpringBootAutoConfiguration.java) |
| 95 | spring | How do you handle exceptions consistently in Spring Boot? | [SpringBootExceptionHandling.md](SpringBootExceptionHandling.md) | [SpringBootExceptionHandling.java](../questions/SpringBootExceptionHandling.java) |
| 96 | api | How do you test Spring Boot services: unit, slice, integration, contract? | [SpringBootTestingStrategy.md](SpringBootTestingStrategy.md) | [SpringBootTestingStrategy.java](../questions/SpringBootTestingStrategy.java) |
| 97 | design | What does Spring Cloud Gateway do in microservices? | [SpringCloudGateway.md](SpringCloudGateway.md) | [SpringCloudGateway.java](../questions/SpringCloudGateway.java) |
| 98 | spring | Explain Spring IoC and dependency injection. | [SpringIocAndDependencyInjection.md](SpringIocAndDependencyInjection.md) | [SpringIocAndDependencyInjection.java](../questions/SpringIocAndDependencyInjection.java) |
| 99 | api | How do JWT scopes map to Spring Security authorities? | [SpringSecurityJWTScopes.md](SpringSecurityJWTScopes.md) | [SpringSecurityJWTScopes.java](../questions/SpringSecurityJWTScopes.java) |
| 100 | spring | Difference between @Component, @Service, @Repository, and @Controller. | [SpringStereotypes.md](SpringStereotypes.md) | [SpringStereotypes.java](../questions/SpringStereotypes.java) |
| 101 | concept | Explain stack memory vs heap memory. | [StackVsHeapMemory.md](StackVsHeapMemory.md) | [StackVsHeapMemory.java](../questions/StackVsHeapMemory.java) |
| 102 | concept | Difference between map, flatMap, filter, reduce, and collect. | [StreamOperations.md](StreamOperations.md) | [StreamOperations.java](../questions/StreamOperations.java) |
| 103 | concept | Explain string interning and when it can hurt. | [StringInterning.md](StringInterning.md) | [StringInterning.java](../questions/StringInterning.java) |
| 104 | concept | Difference between String, StringBuilder, and StringBuffer. | [StringVsBuilderVsBuffer.md](StringVsBuilderVsBuffer.md) | [StringVsBuilderVsBuffer.java](../questions/StringVsBuilderVsBuffer.java) |
| 105 | concurrency | Explain structured concurrency and how it improves Java async code. | [StructuredConcurrency.md](StructuredConcurrency.md) | [StructuredConcurrency.java](../questions/StructuredConcurrency.java) |
| 106 | concurrency | synchronized vs ReentrantLock. | [SynchronizedVsReentrantLock.md](SynchronizedVsReentrantLock.md) | [SynchronizedVsReentrantLock.java](../questions/SynchronizedVsReentrantLock.java) |
| 107 | concurrency | Explain thread-local and its risks in app servers. | [ThreadLocalRisks.md](ThreadLocalRisks.md) | [ThreadLocalRisks.java](../questions/ThreadLocalRisks.java) |
| 108 | concurrency | How do you size thread pools for Java backend services? | [ThreadPoolSizing.md](ThreadPoolSizing.md) | [ThreadPoolSizing.java](../questions/ThreadPoolSizing.java) |
| 109 | concurrency | How do you make a method thread-safe? | [ThreadSafeMethod.md](ThreadSafeMethod.md) | [ThreadSafeMethod.java](../questions/ThreadSafeMethod.java) |
| 110 | api | Transaction propagation and isolation in Spring. | [TransactionPropagationIsolation.md](TransactionPropagationIsolation.md) | [TransactionPropagationIsolation.java](../questions/TransactionPropagationIsolation.java) |
| 111 | api | How does @Transactional work? | [TransactionalBasics.md](TransactionalBasics.md) | [TransactionalBasics.java](../questions/TransactionalBasics.java) |
| 112 | api | Explain the transactional outbox pattern. | [TransactionalOutboxPattern.md](TransactionalOutboxPattern.md) | [TransactionalOutboxPattern.java](../questions/TransactionalOutboxPattern.java) |
| 113 | api | Common pitfalls of @Transactional self-invocation. | [TransactionalSelfInvocation.md](TransactionalSelfInvocation.md) | [TransactionalSelfInvocation.java](../questions/TransactionalSelfInvocation.java) |
| 114 | concept | Explain try-with-resources. | [TryWithResources.md](TryWithResources.md) | [TryWithResources.java](../questions/TryWithResources.java) |
| 115 | api | How do you validate REST request payloads? | [ValidateRestPayloads.md](ValidateRestPayloads.md) | [ValidateRestPayloads.java](../questions/ValidateRestPayloads.java) |
| 116 | concept | When should you use var in Java? | [VarInference.md](VarInference.md) | [VarInference.java](../questions/VarInference.java) |
| 117 | concurrency | What is virtual thread pinning and why does it matter? | [VirtualThreadPinning.md](VirtualThreadPinning.md) | [VirtualThreadPinning.java](../questions/VirtualThreadPinning.java) |
| 118 | concurrency | What are virtual threads in Java 21 and when should you use them? | [VirtualThreadsUseCases.md](VirtualThreadsUseCases.md) | [VirtualThreadsUseCases.java](../questions/VirtualThreadsUseCases.java) |
| 119 | concurrency | What does volatile guarantee and what does it not guarantee? | [VolatileGuarantees.md](VolatileGuarantees.md) | [VolatileGuarantees.java](../questions/VolatileGuarantees.java) |
| 120 | concept | When should you avoid streams? | [WhenToAvoidStreams.md](WhenToAvoidStreams.md) | [WhenToAvoidStreams.java](../questions/WhenToAvoidStreams.java) |
