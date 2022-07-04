Stream.of(
Block.box(2, 0, 7, 14, 5, 12),
Block.box(3.5, 0.1, 1, 12.5, 1.1, 7),
Block.box(12, 0, 12, 15, 5, 14),
Block.box(12, 0, 5, 15, 3, 7),
Block.box(1, 0, 5, 4, 3, 7),
Block.box(1, 0, 12, 4, 5, 14),
Block.box(1.0999999999999996, 0, 7, 14.9, 5, 12),
Block.box(4, 0, 12, 12, 3, 14),
Block.box(3.5, 0.1, 7.5, 12.5, 1.1, 1),
Block.box(1.0099999999999998, 3, 5, 14.99, 5, 7),
Block.box(4, 3, 12, 12, 9.3, 16)
).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();