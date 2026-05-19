# SE1020 OOP Guide — Event Ticket Booking System

## MVC Flow

| Layer | Responsibility | Classes |
|-------|----------------|---------|
| **View** | HTML pages shown to user | `templates/*.html`, `static/css`, `static/js` |
| **Controller** | Handle HTTP requests, call services | `*Controller` |
| **Model** | Data objects | `User`, `Event`, `Booking`, `Person`, `Admin` |
| **Service** | Business logic + CRUD | `*Service` |
| **File** | Read/write `.txt` storage | `FileManager`, `*FileHandler` |

## CRUD Operations (minimum 3 modules)

| Module | Create | Read | Update | Delete |
|--------|--------|------|--------|--------|
| **Events** | Add event | View all / by id | Update event | Delete event |
| **Users** | Register | Login / profile | Update profile (optional) | — |
| **Bookings** | Book tickets | View bookings | — | Cancel booking |

## Three Main OOP Concepts

### 1. Encapsulation
- Use **private** fields in `User`, `Event`, `Booking`, `Person`.
- Expose data only through **getters** and **setters**.
- Hide file path details inside `FileManager` (private path constants).

### 2. Inheritance
```
Person (abstract)
  └── User
        └── Admin
```
- `Person`: common fields (`id`, `name`, `email`, `password`).
- `User`: normal customer behaviour.
- `Admin`: extra method e.g. `canManageEvents()` returning `true`.

### 3. Polymorphism
- **Interface `CrudService<T>`** — implemented by `UserService`, `EventService`, `BookingService`.
- **Interface `FileHandler<T>`** — implemented by `UserFileHandler`, `EventFileHandler`, `BookingFileHandler`.
- **Override** `getRole()` in `User` vs `Admin` (returns `"USER"` / `"ADMIN"`).
- Call `fileHandler.readAll()` on a `FileHandler<?>` reference without knowing the concrete class.

## Recommended Method Signatures (add when implementing)

### Person / User / Admin
- `Person`: `abstract String getRole();`
- `User`: `@Override String getRole()` → `"USER"`
- `Admin`: `@Override String getRole()` → `"ADMIN"`; `boolean canManageEvents()`

### Services (implement `CrudService`)
- `findAll()`, `findById(String id)`, `save(T)`, `update(T)`, `delete(String id)`
- `UserService`: `register(User)`, `login(String email, String password)`, `getProfile(String userId)`
- `BookingService`: `bookTicket(Booking)`, `getBookingsByUser(String userId)`, `cancelBooking(String id)`

### Controllers (mapping examples)
| Controller | URL examples |
|------------|----------------|
| `HomeController` | `GET /` |
| `UserController` | `/users/register`, `/users/login`, `/users/profile` |
| `EventController` | `/events`, `/events/add`, `/events/edit/{id}`, `/events/delete/{id}` |
| `BookingController` | `/bookings/book`, `/bookings`, `/bookings/cancel/{id}` |

## IntelliJ IDEA Setup

1. **File → Open** → select project folder (contains `pom.xml`).
2. Trust Maven import; wait for dependencies.
3. Run `EventTicketBookingApplication`.
4. Open browser: `http://localhost:8080`
