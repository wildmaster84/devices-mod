Stream.of(
Block.box(7, 0, 2, 12, 5, 14),
Block.box(1, 0.1, 3.5, 7, 1.1, 12.5),
Block.box(12, 0, 1, 14, 5, 4),
Block.box(5, 0, 1, 7, 3, 4),
Block.box(5, 0, 12, 7, 3, 15),
Block.box(12, 0, 12, 14, 5, 15),
Block.box(7, 0, 1.0999999999999996, 12, 5, 14.9),
Block.box(12, 0, 4, 14, 3, 12),
Block.box(7.5, 0.1, 3.5, 1, 1.1, 12.5),
Block.box(5, 3, 1.0099999999999998, 7, 5, 14.99),
Block.box(12, 3, 4, 16, 9.3, 12)
).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();