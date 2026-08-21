# Animated spacing implementation

`AnimatedSpacingColumn` and `AnimatedSpacingRow` use one animated presence value per scoped `AnimatedVisibility` child.
That value drives three parts of the same transition: the wrapper's occupied main-axis size, its contribution to adjacent
spacing, and optional alpha. Exiting content remains composed until the presence transition reaches its hidden endpoint.

## Measurement and spacing

The parent measure policy follows Compose Foundation's `Row`/`Column` behavior for ordinary children: fixed children
are measured progressively under tight constraints, and normal weights use Foundation-aligned allocation, rounding,
`fill`, and unbounded-axis semantics.

Animated gaps are calculated from both layout directions and averaged so entering or leaving a child affects the gaps
on both sides symmetrically. Pixel rounding is corrected as a group. For presence values `p` and pixel spacing `s`, the
rounded gaps therefore retain this exact total:

```text
round(max(sum(p) - 1, 0) * s)
```

The visibility wrapper measures its content at full size, reports a presence-scaled main-axis size, and clips and places
the content according to the configured structural alignment. The transition's origin determines whether `expandFrom`
or `shrinkTowards` is used, which keeps the anchor stable during rapid reversals.

## Weights and cost

Layouts containing only ordinary weights use an O(n) measurement path. That path remains active even when other,
unweighted children use scoped visibility.

The heavier animated redistribution runs only when a visibility-controlled child itself has weight. It compares each
transitioning weighted allocation with the weighted recipients, making that branch O(n²). Released space is distributed
according to recipient weight and presence. The wrapper's full-size measurement is derived from its occupied allocation
so multiple partially visible weighted children realize the redistribution result exactly.

## Animation mechanism

The implementation does not use lookahead or drive structural animation by conditionally recomposing children. Presence
state is consumed during measurement, while each scoped visibility wrapper uses a graphics layer for clipping and,
when enabled, alpha. This keeps ordinary direct children ordinary; only scoped `AnimatedVisibility` children participate
in structural visibility and animated spacing.
