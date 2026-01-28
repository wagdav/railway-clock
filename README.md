# Swiss Railway Clock

<https://en.wikipedia.org/wiki/Swiss_railway_clock>

## Development

Interactive development
```
nix develop --command clj -M:shadow-cljs watch app
```

Update dependencies

```
nix develop --command clj2nix deps.edn deps.nix
```
