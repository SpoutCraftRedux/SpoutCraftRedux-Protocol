# SpoutCraft Redux Protocol

The wire format shared by the [SpoutCraft Redux server](https://github.com/SpoutCraftRedux/SpoutCraftRedux-Server) and the [Fabric client](https://github.com/SpoutCraftRedux/SpoutCraftRedux-Client).

It is plain Java 25 with no Minecraft dependency. Messages encode to byte arrays, which each side wraps in a vanilla custom payload. Every decoder checks lengths, counts and trailing bytes against the caps in `Limits`, and throws `ProtocolException` when a message breaks one.

The client and server include this repository as a git submodule at `protocol/` and compile its sources directly, so a given client or server commit always builds against a known protocol commit.

## Handshake

The server sends `spoutcraftredux:hello` during the configuration phase with the protocol range and capabilities it supports. A client with the mod answers with `spoutcraftredux:hello_ack`, naming the version it picked, its own capabilities and its mod version. Vanilla clients ignore the hello.

Bump `SpoutCraftReduxProtocol.VERSION` when a message changes in a way older peers can't read. New features should get a capability string in `Capabilities` rather than a version bump.

## Building

```
./gradlew test
```

## License

LGPL-3.0. See [LICENSE](LICENSE) and [COPYING](COPYING).
