<h1 align="center">
  <img width="250" src="https://rawgit.com/hastebrot/protokola/master/doc/protologo.png" alt="Protokola">
  <br>
</h1>

<h4 align="center">Message Infrastructure</h4>

<p align="center">
  <a href="https://travis-ci.org/hastebrot/protokola">
    <img
      src="https://img.shields.io/travis/hastebrot/protokola/master.svg"
      alt="Travis Build" />
  </a>

  <a href="https://github.com/canoo">
    <img
      src="https://img.shields.io/badge/canoo-incubator-yellow.svg?style=flat"
      alt="Canoo Incubator" />
  </a>

  <a href="http://www.apache.org/licenses/LICENSE-2.0">
    <img
      src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"
      alt="Apache License 2.0" />
  </a>
</p>

<!-- TOC depthFrom:2 depthTo:2 -->

- [What is Protokola?](#what-is-protokola)
- [Roadmap](#roadmap)
- [Implementation Status](#implementation-status)
- [About this Repository](#about-this-repository)

<!-- /TOC -->

## What is Protokola?

Protokola is an experimental message infrastructure.

## Roadmap

**Proof of concept (November 2017 &ndash; February 2018):**

- [x] Implement a store that contains messages.
- [x] Implement a channel that fills the store.
- [x] Implement a codec that reads and writes messages.
- [x] Implement a observable that gets and sets values.
- [ ] Implement a registry that manages observables and property paths.

## Implementation Status

Components for a "Dolphin Platform 1.x"-compatible server and client.

Component | Features
----------|----------
Channel | :sunny: ClientRequest, :sunny: ServerResponse, :sunny: DolphinClientId, :sunny: SessionCookie.
Codec | :sunny: CreatePresentationalModel, :umbrella: CreateContext, :umbrella: DestroyContext, :umbrella: CreateController, :umbrella: DestroyController, :sunny: DestroyPresentationalModelCommand, :umbrella: ValueChanged, :umbrella: CallAction, :umbrella: StartLongPoll, :umbrella: InterruptLongPoll.
Observable | :sunny: get(value), :sunny: set(value), :sunny: push(array), :sunny: pop(array), :sunny: splice(array).
Registry | :umbrella: Observable Property Registry, :umbrella: Property Path Registry.

## About this Repository

Protokola currently exists as a mono-repository.

- `protokola.message`
- `protokola.observable`
- `protokola.property`
- `protokola.registry`
- `protokola.transport`
