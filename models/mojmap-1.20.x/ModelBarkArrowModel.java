
public class ModelBarkArrowModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "barkarrow"), "main");
	private final ModelPart cross_1;
	private final ModelPart cross_2;

	public ModelBarkArrowModel(ModelPart root) {
		this.cross_1 = root.getChild("cross_1");
		this.cross_2 = root.getChild("cross_2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		// Adding cross_1
		partDefinition.addOrReplaceChild("cross_1",
				CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -2.3536F, 0.3536F, 16.0F, 4.0F, 0.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.0F, 20.5F, 0.0F, -2.3562F, 0.0F, 1.5708F));

		// Adding cross_2
		PartDefinition cross_2 = partDefinition.addOrReplaceChild("cross_2", CubeListBuilder.create(),
				PartPose.offsetAndRotation(0.0F, 20.5F, -4.0F, -0.7854F, 0.0F, 0.0F));

		// Adding cross_2_r1 as a child of cross_2
		cross_2.addOrReplaceChild("cross_2_r1",
				CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -2.0F, 0.0F, 16.0F, 4.0F, 0.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-4.0F, -3.0F, 2.5F, -0.9671F, -0.7904F, 1.581F));

		return LayerDefinition.create(meshDefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		// Animations can be added here if necessary
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		cross_1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		cross_2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
