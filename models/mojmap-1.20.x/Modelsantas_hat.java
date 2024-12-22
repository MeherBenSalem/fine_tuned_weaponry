// Made with Blockbench 4.3.0
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports

public class Modelsantas_hat<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in
	// the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "santas_hat"), "main");
	private final ModelPart santas_hat;

	public Modelsantas_hat(ModelPart root) {
		this.santas_hat = root.getChild("santas_hat");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition santas_hat = partdefinition.addOrReplaceChild("santas_hat",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(-6.0F, -10.0F, -7.0F, 11.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)).texOffs(0, 14)
						.addBox(-5.0F, -12.0F, -6.0F, 9.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(22, 34)
						.addBox(-8.0F, -15.0F, -2.0F, 8.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(22, 26)
						.addBox(-3.0F, -14.0F, -4.0F, 5.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(0, 26)
						.addBox(-12.0F, -16.0F, -4.0F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		santas_hat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}