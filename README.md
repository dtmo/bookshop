# Bookshop

This repository is intended to be an example implementation of a book shop app
using Spring Boot and, possibly also, Quarkus.

## Use Cases

Initial thoughts on use cases for the app can be found in:
[Book Shop Use Cases](docs/design/book_shop_use_cases.md)

## Domain Model

A model capturing details of the Book Shop domain can be found in:
[Book Shop Domain Model](docs/design/book_shop_domain_model.md)

## Data Persistence

Details of the approach for data persistence can be foun in:
[Book Shop Data Persistence](docs/design/book_shop_data_persistence.md)

## Updating design docs

The design doc diagrams are generated using [PlantUML](https://plantuml/com).
When a diagram is updated, it needs to be re-exported (as SVG to play nicely
with Git) and then committed to the repository.

If using VSCode then the
[PlantUML extension](https://marketplace.visualstudio.com/items?itemName=jebbs.plantuml)
provides a convenient mechanism for exporting diagrams using the
`PlantUML: Export Current Diagram` command.
