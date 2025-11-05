package fr.unice.polytech.sophiatecheats.infrastructure.config;

import fr.unice.polytech.sophiatecheats.application.usecases.order.ConfirmOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.order.DeliverOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.order.PlaceOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.BrowseRestaurantsUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.cart.*;
import fr.unice.polytech.sophiatecheats.domain.repositories.OrderRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.services.*;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryOrderRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryRestaurantRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryUserRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryCartRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.services.*;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

/**
 * Central application configuration managing dependency injection for the SophiaTech Eats system.
 * 
 * <p>This configuration class implements the Dependency Inversion Principle by wiring
 * concrete implementations to their corresponding interfaces. It uses PicoContainer
 * for lightweight dependency injection following Clean Architecture guidelines.</p>
 * 
 * <h3>Dependency Flow:</h3>
 * <pre>
 * Infrastructure → Application → Domain
 * </pre>
 * 
 * <p>All dependencies are configured to flow inward toward the domain layer,
 * ensuring business logic remains framework-independent.</p>
 * 
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
public class ApplicationConfig {

  private final MutablePicoContainer container;

  public ApplicationConfig() {
    this.container = new DefaultPicoContainer();
    configure();
  }

  /**
   * Configure l'injection de dépendances.
   * Enregistre les implementations concrètes pour les interfaces.
   */
  private void configure() {
    // Repositories
    container.addComponent(UserRepository.class, InMemoryUserRepository.class);
    container.addComponent(RestaurantRepository.class, InMemoryRestaurantRepository.class);
    container.addComponent(OrderRepository.class, InMemoryOrderRepository.class);
    container.addComponent(CartRepository.class, InMemoryCartRepository.class);

    // Services - Register concrete implementations
    container.addComponent(PaymentService.class);
    container.addComponent(EmailService.class, EmailServiceImpl.class);
    container.addComponent(SmsService.class, SmsServiceImpl.class);
    container.addComponent(PushNotificationService.class, PushNotificationServiceImpl.class);
    container.addComponent(NotificationService.class, MultiChannelNotificationService.class);

    // Use Cases - Order Management
    container.addComponent(PlaceOrderUseCase.class);
    container.addComponent(ConfirmOrderUseCase.class);
    container.addComponent(DeliverOrderUseCase.class);

    // Use Cases - Restaurant Browsing
    container.addComponent(BrowseRestaurantsUseCase.class);

    // Use Cases - Cart Management
    container.addComponent(AddDishToCartUseCase.class);
    container.addComponent(UpdateCartItemUseCase.class);
    container.addComponent(RemoveFromCartUseCase.class);
    container.addComponent(GetCartUseCase.class);
    container.addComponent(ClearCartUseCase.class);
  }

  /**
   * Récupère une instance configurée d'une classe.
   */
  public <T> T getInstance(Class<T> clazz) {
    return container.getComponent(clazz);
  }

  /**
   * Enregistre manuellement un composant.
   */
  public <T> void registerComponent(Class<T> interfaceClass, Class<? extends T> implementationClass) {
    container.addComponent(interfaceClass, implementationClass);
  }
}
