Stream.of(
Block.box(4, 0, 2, 9, 5, 14),
Block.box(9, 0.1, 3.5, 15, 1.1, 12.5),
Block.box(2, 0, 12, 4, 5, 15),
Block.box(9, 0, 12, 11, 3, 15),
Block.box(9, 0, 1, 11, 3, 4),
Block.box(2, 0, 1, 4, 5, 4),
Block.box(4, 0, 1.0999999999999996, 9, 5, 14.9),
Block.box(2, 0, 4, 4, 3, 12),
Block.box(15, 0.1, 3.5, 8.5, 1.1, 12.5),
Block.box(9, 3, 1.0099999999999998, 11, 5, 14.99),
Block.box(0, 3, 4, 4, 9.3, 12)
).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();